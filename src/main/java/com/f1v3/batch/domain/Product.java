package com.f1v3.batch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products")
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

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "brand")
    private String brand;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "product_status")
    private String productStatus = "ACTIVE";

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "regular_price", precision = 10, scale = 2)
    private BigDecimal regularPrice;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "material")
    private String material;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_new_arrival")
    private Boolean isNewArrival = false;

    @Column(name = "is_best_seller")
    private Boolean isBestSeller = false;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "sales_count")
    private Long salesCount = 0L;

    @Column(name = "tags")
    private String tags;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;

    @Column(name = "shipping_weight")
    private Double shippingWeight;

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "min_order_quantity")
    private Integer minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity;

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    @Builder
    public Product(String name, String description, BigDecimal price, Long sellerId, Long pendingProductId,
                   String productCode, String brand, String manufacturer, String category, String subCategory,
                   String productStatus, Integer stockQuantity, BigDecimal regularPrice, Integer discountRate,
                   BigDecimal salePrice, Double weight, String dimensions, String color, String size,
                   String material, String originCountry, LocalDate releaseDate, LocalDate expiryDate,
                   Boolean isFeatured, Boolean isNewArrival, Boolean isBestSeller, BigDecimal rating,
                   Integer reviewCount, Long viewCount, Long salesCount, String tags, String keywords,
                   String metaTitle, String metaDescription, Integer warrantyPeriod, Double shippingWeight,
                   BigDecimal shippingFee, Integer minOrderQuantity, Integer maxOrderQuantity,
                   LocalDateTime lastRestockedAt) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.pendingProductId = pendingProductId;
        this.productCode = productCode;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.category = category;
        this.subCategory = subCategory;
        this.productStatus = productStatus != null ? productStatus : "ACTIVE";
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.regularPrice = regularPrice;
        this.discountRate = discountRate;
        this.salePrice = salePrice;
        this.weight = weight;
        this.dimensions = dimensions;
        this.color = color;
        this.size = size;
        this.material = material;
        this.originCountry = originCountry;
        this.releaseDate = releaseDate;
        this.expiryDate = expiryDate;
        this.isFeatured = isFeatured != null ? isFeatured : false;
        this.isNewArrival = isNewArrival != null ? isNewArrival : false;
        this.isBestSeller = isBestSeller != null ? isBestSeller : false;
        this.rating = rating != null ? rating : BigDecimal.ZERO;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.viewCount = viewCount != null ? viewCount : 0L;
        this.salesCount = salesCount != null ? salesCount : 0L;
        this.tags = tags;
        this.keywords = keywords;
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
        this.warrantyPeriod = warrantyPeriod;
        this.shippingWeight = shippingWeight;
        this.shippingFee = shippingFee;
        this.minOrderQuantity = minOrderQuantity != null ? minOrderQuantity : 1;
        this.maxOrderQuantity = maxOrderQuantity;
        this.lastRestockedAt = lastRestockedAt;
    }

    // fixme: 보류 -> 실제 테이블로 옮길 때 사용
    //      추후에 필드 추가하기
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
