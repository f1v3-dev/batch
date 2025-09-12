package com.f1v3.batch.service;

import com.f1v3.batch.domain.Product;
import com.f1v3.batch.repository.product.ProductBulkRepository;
import com.f1v3.batch.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductBulkRepository bulkRepository;
    private final ProductRepository productRepository;

    public void createProducts(int count) {
        List<Product> products = createRandomProducts(count);
        bulkRepository.bulkInsertWithPreparedStatement(products);
    }

    private List<Product> createRandomProducts(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Product.builder()
                        .name("테스트 상품 " + i)
                        .description("테스트 상품 설명 " + i + " - 이것은 성능 테스트를 위한 더미 데이터입니다.")
                        .price(BigDecimal.valueOf(1000L + (i % 10000)))
                        .sellerId((long) (i % 1000 + 1))
                        .pendingProductId((long) (i + 1))
                        .build())
                .toList();
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAllProducts();
    }
}
