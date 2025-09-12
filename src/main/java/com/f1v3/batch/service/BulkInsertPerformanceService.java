package com.f1v3.batch.service;

import com.f1v3.batch.domain.Product;
import com.f1v3.batch.dto.BulkInsertResult;
import com.f1v3.batch.dummy.ProductDummy;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import com.f1v3.batch.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkInsertPerformanceService {

    private final ProductBulkRepository productBulkRepository;
    private final ProductRepository productRepository;

    public BulkInsertResult testPreparedStatement(int totalCount, int batchSize) {
        log.info("=== JDBC PreparedStatement 테스트 시작 ===");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalCount; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalCount);
            List<Product> products = ProductDummy.createProducts(endIndex - i);

            long batchStartTime = System.currentTimeMillis();
            productBulkRepository.bulkInsertWithPreparedStatement(products);
            long batchEndTime = System.currentTimeMillis();

            log.info("JDBC PreparedStatement - 배치 {}/{} 완료 ({}ms)",
                    (i / batchSize) + 1,
                    (totalCount / batchSize),
                    batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long finalCount = productRepository.count();

        log.info("=== JDBC PreparedStatement 완료 ===");
        log.info("총 소요시간: {}ms ({} 초)", totalTime, totalTime / 1000.0);
        log.info("삽입된 레코드 수: {}", finalCount);
        log.info("초당 처리량: {}/sec", (finalCount * 1000) / totalTime);

        return BulkInsertResult.builder()
                .method("JDBC PreparedStatement")
                .totalCount(totalCount)
                .batchSize(batchSize)
                .totalTimeMs(totalTime)
                .totalTimeSec(totalTime / 1000.0)
                .insertedRecords(finalCount)
                .throughputPerSec((finalCount * 1000) / totalTime)
                .build();
    }

    public BulkInsertResult testStatement(int totalCount, int batchSize) {
        log.info("=== JDBC Statement 테스트 시작 ===");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalCount; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalCount);
            List<Product> products = ProductDummy.createProducts(endIndex - i);

            long batchStartTime = System.currentTimeMillis();
            productBulkRepository.bulkInsertWithStatement(products);
            long batchEndTime = System.currentTimeMillis();

            log.info("JDBC Statement - 배치 {}/{} 완료 ({}ms)",
                    (i / batchSize) + 1,
                    (totalCount / batchSize),
                    batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long finalCount = productRepository.count();

        log.info("=== JDBC Statement 완료 ===");
        log.info("총 소요시간: {}ms ({} 초)", totalTime, totalTime / 1000.0);
        log.info("삽입된 레코드 수: {}", finalCount);
        log.info("초당 처리량: {}/sec", (finalCount * 1000) / totalTime);

        return BulkInsertResult.builder()
                .method("JDBC Statement")
                .totalCount(totalCount)
                .batchSize(batchSize)
                .totalTimeMs(totalTime)
                .totalTimeSec(totalTime / 1000.0)
                .insertedRecords(finalCount)
                .throughputPerSec((finalCount * 1000) / totalTime)
                .build();
    }

    @Transactional(readOnly = true)
    public long getCurrentRecordCount() {
        return productRepository.count();
    }
}

