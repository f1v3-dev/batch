package com.f1v3.batch.controller;

import com.f1v3.batch.service.PendingProductService;
import com.f1v3.batch.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final PendingProductService pendingProductService;

    @PostMapping("/api/products")
    public ResponseEntity<Void> createProducts(@RequestParam(required = false, defaultValue = "100000") int count) {
        productService.createProducts(count);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/products/mixed-status")
    public ResponseEntity<Void> createMixedStatusProducts() {
        pendingProductService.createTwoMillionMixedStatusProducts();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/products/unique")
    public ResponseEntity<Void> createUniqueProducts() {
        pendingProductService.createUniqueProducts();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/products/pending")
    public ResponseEntity<Void> createPendingProducts() {
        pendingProductService.createMillionPendingProducts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/products/pending/search/v1")
    public ResponseEntity<?> searchPendingProductsV1(@RequestParam String keyword) {
        return ResponseEntity.ok(pendingProductService.findByNameAndDescription(keyword));
    }

    @GetMapping("/api/products/pending/search/v2")
    public ResponseEntity<?> searchPendingㅇProductsV2(@RequestParam String keyword) {
        return ResponseEntity.ok(pendingProductService.findByNameAndDescriptionV2(keyword));
    }
}
