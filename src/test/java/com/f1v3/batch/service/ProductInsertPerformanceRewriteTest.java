package com.f1v3.batch.service;

import com.f1v3.batch.domain.Product;
import com.f1v3.batch.dummy.ProductDummy;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import com.f1v3.batch.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test-rewrite")
class ProductInsertPerformanceRewriteTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBulkRepository productBulkRepository;

    private static final int TOTAL_COUNT = 1_000_000; // 100만개
    private static final int BATCH_SIZE = 10_000; // 배치 사이즈

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        System.out.println("테스트 데이터 초기화 완료");
    }

    @Test
    @DisplayName("JPA saveAll() 성능 테스트 - rewriteBatchedStatements=true")
    void testJpaSaveAllPerformance() {
        System.out.println("=== JPA saveAll() 성능 테스트 시작 (rewriteBatchedStatements=true) ===");

        long startTime = System.currentTimeMillis();

        // 메모리 효율성을 위해 배치 단위로 처리
        for (int i = 0; i < TOTAL_COUNT; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, TOTAL_COUNT);
            List<Product> products = ProductDummy.createProducts(endIndex - i);

            long batchStartTime = System.currentTimeMillis();
            productRepository.saveAll(products);
            long batchEndTime = System.currentTimeMillis();

            System.out.printf("JPA saveAll - 배치 %d/%d 완료 (%dms)%n",
                    (i / BATCH_SIZE) + 1,
                    (TOTAL_COUNT / BATCH_SIZE),
                    batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long finalCount = productRepository.count();
        System.out.println("=== JPA saveAll() 완료 ===");
        System.out.printf("총 소요시간: %dms (%.1f초)%n", totalTime, totalTime / 1000.0);
        System.out.printf("삽입된 레코드 수: %d%n", finalCount);
        System.out.printf("초당 처리량: %d/sec%n", (finalCount * 1000) / totalTime);
    }

    @Test
    @DisplayName("JDBC Statement 성능 테스트 - rewriteBatchedStatements=true")
    void testJdbcStatementPerformance() {
        System.out.println("=== JDBC Statement 성능 테스트 시작 (rewriteBatchedStatements=true) ===");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_COUNT; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, TOTAL_COUNT);
            List<Product> products = ProductDummy.createProducts(endIndex - i);

            long batchStartTime = System.currentTimeMillis();
            productBulkRepository.bulkInsertWithStatement(products);
            long batchEndTime = System.currentTimeMillis();

            System.out.printf("JDBC Statement - 배치 %d/%d 완료 (%dms)%n",
                    (i / BATCH_SIZE) + 1,
                    (TOTAL_COUNT / BATCH_SIZE),
                    batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long finalCount = productRepository.count();
        System.out.println("=== JDBC Statement 완료 ===");
        System.out.printf("총 소요시간: %dms (%.1f초)%n", totalTime, totalTime / 1000.0);
        System.out.printf("삽입된 레코드 수: %d%n", finalCount);
        System.out.printf("초당 처리량: %d/sec%n", (finalCount * 1000) / totalTime);
    }

    @Test
    @DisplayName("JDBC PreparedStatement 성능 테스트 - rewriteBatchedStatements=true")
    void testJdbcPreparedStatementPerformance() {
        System.out.println("=== JDBC PreparedStatement 성능 테스트 시작 (rewriteBatchedStatements=true) ===");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_COUNT; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, TOTAL_COUNT);
            List<Product> products = ProductDummy.createProducts(endIndex - i);

            long batchStartTime = System.currentTimeMillis();
            productBulkRepository.bulkInsertWithPreparedStatement(products);
            long batchEndTime = System.currentTimeMillis();

            System.out.printf("JDBC PreparedStatement - 배치 %d/%d 완료 (%dms)%n",
                    (i / BATCH_SIZE) + 1,
                    (TOTAL_COUNT / BATCH_SIZE),
                    batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long finalCount = productRepository.count();
        System.out.println("=== JDBC PreparedStatement 완료 ===");
        System.out.printf("총 소요시간: %dms (%.1f초)%n", totalTime, totalTime / 1000.0);
        System.out.printf("삽입된 레코드 수: %d%n", finalCount);
        System.out.printf("초당 처리량: %d/sec%n", (finalCount * 1000) / totalTime);
    }
}
