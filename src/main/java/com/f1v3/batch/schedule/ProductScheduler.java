package com.f1v3.batch.schedule;

import com.f1v3.batch.domain.PendingProduct;
import com.f1v3.batch.domain.Product;
import com.f1v3.batch.domain.enums.ProductStatus;
import com.f1v3.batch.repository.pendingproduct.PendingProductRepository;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private static final int BATCH_SIZE = 1000;
    private final PendingProductRepository pendingProductRepository;
    private final ProductBulkRepository productBulkRepository;


    /**
     * 매일 새벽 2시에 pending_products 테이블의 status가 'APPROVED'인 상품들을
     * 실제 서비스 테이블인 products 테이블로 적재하는 배치 작업을 수행합니다.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void batchInsertApprovedProducts() {

        // 1. PendingProductRepository에서 status가 'APPROVED'인 상품들을 전체 조회

        // todo: 모든 상품을 한꺼번에 메모리에 올리는게 맞을까?
        //   반대로 짤라서 메모리에 올린다면, 중간에 새로운 데이터들이 추가/삭제/수정될 경우 어떻게 될까?
        List<PendingProduct> pendingProducts = pendingProductRepository.findByStatus(ProductStatus.APPROVED);


        // 2. 조회된 상품들을 BATCH_SIZE 단위로 나누어 ProductRepository를 통해 products 테이블에 적재
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
}
