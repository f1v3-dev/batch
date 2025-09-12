package com.f1v3.batch.controller;

import com.f1v3.batch.dto.BulkInsertResult;
import com.f1v3.batch.service.BulkInsertPerformanceService;
import com.f1v3.batch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class BulkInsertPerformanceController {

    private final BulkInsertPerformanceService bulkInsertPerformanceService;
    private final ProductService productService;

    @PostMapping("/bulk-insert/prepared-statement")
    public BulkInsertResult testPreparedStatement(
            @RequestParam(defaultValue = "1000000") int totalCount,
            @RequestParam(defaultValue = "10000") int batchSize) {

        return bulkInsertPerformanceService.testPreparedStatement(totalCount, batchSize);
    }

    @PostMapping("/bulk-insert/statement")
    public BulkInsertResult testStatement(
            @RequestParam(defaultValue = "10000") int totalCount,
            @RequestParam(defaultValue = "1000") int batchSize) {

        return bulkInsertPerformanceService.testStatement(totalCount, batchSize);
    }

    @GetMapping("/status")
    public Map<String, Long> getStatus() {
        Map<String, Long> status = new HashMap<>();
        long currentCount = bulkInsertPerformanceService.getCurrentRecordCount();
        status.put("currentRecordCount", currentCount);
        return status;
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        productService.deleteAllProducts();
        return ResponseEntity.ok().build();
    }
}
