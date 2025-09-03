package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class ProductDummy {

    public static List<Product> createProducts(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Product.builder()
                        .name("테스트 상품 " + i)
                        .description("테스트 상품 설명 " + i + " - 이것은 성능 테스트를 위한 더미 데이터입니다.")
                        .price(BigDecimal.valueOf(1000 + (i % 10000)))
                        .sellerId((long) (i % 1000 + 1))
                        .pendingProductId((long) (i + 1))
                        .build())
                .toList();
    }
}
