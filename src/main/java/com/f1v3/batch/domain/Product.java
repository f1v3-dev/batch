package com.f1v3.batch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "products",
        indexes = {
                @Index(
                        name = "idx_products_name_description",
                        columnList = "name, description"
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "pending_product_id", nullable = false)
    private Long pendingProductId;

    @Builder
    public Product(String name, String description, BigDecimal price, Long sellerId, Long pendingProductId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.pendingProductId = pendingProductId;
    }

    public static Product fromPendingProduct(PendingProduct pendingProduct) {
        return Product.builder()
                .name(pendingProduct.getName())
                .description(pendingProduct.getDescription())
                .price(pendingProduct.getPrice())
                .sellerId(pendingProduct.getSellerId())
                .pendingProductId(pendingProduct.getId())
                .build();
    }
}
