package com.f1v3.batch.controller;

import com.f1v3.batch.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/products")
    public ResponseEntity<Void> createProducts(@RequestParam(required = false, defaultValue = "100000") int count) {
        productService.createProducts(count);
        return ResponseEntity.ok().build();
    }
}
