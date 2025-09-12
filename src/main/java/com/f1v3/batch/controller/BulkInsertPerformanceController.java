package com.f1v3.batch.controller;

import com.f1v3.batch.domain.Product;
import com.f1v3.batch.dummy.ProductDummy;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import com.f1v3.batch.repository.product.ProductRepository;
import com.f1v3.batch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class BulkInsertPerformanceController {

    private final ProductBulkRepository productBulkRepository;
    private final ProductRepository productRepository;

    private static final String STATUS_KEY = "status";
    private static final String COMPLETED_STATUS = "completed";
    private final ProductService productService;

    @PostMapping("/bulk-insert/prepared-statement")
    public Map<String, Object> testUseServerPrepStmts(
            @RequestParam(defaultValue = "1000000") int totalCount,
            @RequestParam(defaultValue = "10000") int batchSize) {

        log.info("=== useServerPrepStmts 설정 테스트 시작 ===");

        long startTime = System.currentTimeMillis();

        // 테스트 전 기존 데이터 삭제
        productService.deleteAllProducts();

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

        // 결과를 JSON으로 반환
        Map<String, Object> result = new HashMap<>();
        result.put("method", "JDBC PreparedStatement");
        result.put("totalCount", totalCount);
        result.put("batchSize", batchSize);
        result.put("totalTimeMs", totalTime);
        result.put("totalTimeSec", totalTime / 1000.0);
        result.put("insertedRecords", finalCount);
        result.put("throughputPerSec", (finalCount * 1000) / totalTime);
        result.put(STATUS_KEY, COMPLETED_STATUS);

        return result;
    }

    @PostMapping("/bulk-insert/statement")
    public Map<String, Object> testStatement(
            @RequestParam(defaultValue = "10000") int totalCount,
            @RequestParam(defaultValue = "1000") int batchSize) {

        log.info("=== Statement 설정 테스트 시작 ===");

        long startTime = System.currentTimeMillis();

        // 테스트 전 기존 데이터 삭제
        productService.deleteAllProducts();

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

        // 결과를 JSON으로 반환
        Map<String, Object> result = new HashMap<>();
        result.put("method", "JDBC Statement");
        result.put("totalCount", totalCount);
        result.put("batchSize", batchSize);
        result.put("totalTimeMs", totalTime);
        result.put("totalTimeSec", totalTime / 1000.0);
        result.put("insertedRecords", finalCount);
        result.put("throughputPerSec", (finalCount * 1000) / totalTime);
        result.put(STATUS_KEY, COMPLETED_STATUS);

        return result;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        long currentCount = productRepository.count();
        status.put("currentRecordCount", currentCount);
        status.put(STATUS_KEY, "ready");
        return status;
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        productService.deleteAllProducts();

        return ResponseEntity.ok().build();
    }
}
