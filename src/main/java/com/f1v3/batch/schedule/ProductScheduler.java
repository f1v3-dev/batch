package com.f1v3.batch.schedule;

import com.f1v3.batch.domain.PendingProduct;
import com.f1v3.batch.domain.Product;
import com.f1v3.batch.domain.enums.ProductStatus;
import com.f1v3.batch.exception.BatchProcessingException;
import com.f1v3.batch.repository.pendingproduct.PendingProductRepository;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private static final int BATCH_SIZE = 1000;
    private final PendingProductRepository pendingProductRepository;
    private final ProductBulkRepository productBulkRepository;

    private final DataSource dataSource;


    /**
     * 매일 새벽 2시에 pending_products 테이블의 status가 'APPROVED'인 상품들을
     * 실제 서비스 테이블인 products 테이블로 적재하는 배치 작업을 수행합니다.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void batchInsertApprovedProducts() {

        // 1. PendingProductRepository에서 status가 'APPROVED'인 상품들을 전체 조회

        // todo: 모든 상품을 한꺼번에 메모리에 올리는게 맞을까?
        //   반대로 짤라서 메모리에 올린다면, 중간에 새로운 데이터들이 추가/삭제/수정될 경우 어떻게 될까? (상관 없나..)
        //   클라이언트 커서 방식 vs 스트리밍 방식 비교해보면 좋을거같음 (데이터를 조회하는 방법)

        // (1) findAll()
        List<PendingProduct> pendingProducts = pendingProductRepository.findByStatus(ProductStatus.APPROVED);

        // 2. 조회된 상품들을 BATCH_SIZE 단위로 ProductRepository를 통해 products 테이블에 적재
        int totalProducts = pendingProducts.size();

        for (int i = 0; i < totalProducts; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, totalProducts);
            List<PendingProduct> batch = pendingProducts.subList(i, endIndex);

            // Product 엔티티로 변환
            List<Product> productsToInsert = batch.stream()
                    .map(pendingProduct -> {
                        pendingProduct.executeProcessing();
                        return Product.fromPendingProduct(pendingProduct);
                    })
                    .toList();

            productBulkRepository.bulkInsertWithPreparedStatement(productsToInsert);
        }
    }

    /**
     * MySQL 스트리밍 방식을 사용하여 메모리 효율적으로 배치 작업을 수행합니다.
     * 대용량 데이터를 한 번에 메모리에 올리지 않고, 스트리밍으로 처리합니다.
     */
    public void batchInsertApprovedProductsWithStreaming() {
        String sql = """
                SELECT *
                FROM pending_products
                WHERE status = 'APPROVED'
                ORDER BY id
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql,
                     ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            // MySQL 스트리밍을 위한 설정
            statement.setFetchSize(Integer.MIN_VALUE); // MySQL 드라이버에게 스트리밍 모드를 알림

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Product> batchProducts = new ArrayList<>();
                int processedCount = 0;

                while (resultSet.next()) {
                    // ResultSet에서 PendingProduct 객체 생성
                    PendingProduct pendingProduct = createPendingProductFromResultSet(resultSet);

                    pendingProduct.executeProcessing();
                    Product product = Product.fromPendingProduct(pendingProduct);
                    batchProducts.add(product);

                    // 배치 사이즈에 도달하면 일괄 삽입
                    if (batchProducts.size() >= BATCH_SIZE) {
                        productBulkRepository.bulkInsertWithPreparedStatement(batchProducts);
                        processedCount += batchProducts.size();

                        log.info("배치 처리 진행 중: {}개 상품 처리 완료", processedCount);
                        batchProducts.clear();
                    }
                }

                // 남은 데이터 처리
                if (!batchProducts.isEmpty()) {
                    productBulkRepository.bulkInsertWithPreparedStatement(batchProducts);
                    processedCount += batchProducts.size();
                }

                log.info("스트리밍 배치 작업 완료: 총 {}개 상품 처리", processedCount);

            }
        } catch (SQLException e) {
            log.error("스트리밍 배치 작업 중 오류 발생", e);
            throw new BatchProcessingException("배치 작업 실패", e);
        }
    }

    private PendingProduct createPendingProductFromResultSet(ResultSet resultSet) throws SQLException {
        return PendingProduct.builder()
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .price(resultSet.getBigDecimal("price"))
                .sellerId(resultSet.getLong("seller_id"))
                .build();
    }
}
