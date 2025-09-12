package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ProductDummy {

    private ProductDummy() {
        // Utility class
    }

    private static final Random random = new Random();

    private static final String[] BRANDS = {"삼성", "LG", "애플", "나이키", "아디다스", "유니클로", "무지", "코카콜라", "펩시", "네스틀"};
    private static final String[] CATEGORIES = {"전자제품", "의류", "식품", "생활용품", "스포츠", "도서", "완구", "화장품", "가구", "자동차용품"};
    private static final String[] COLORS = {"빨강", "파랑", "노랑", "검정", "흰색", "회색", "초록", "보라", "분홍", "주황"};
    private static final String[] SIZES = {"XS", "S", "M", "L", "XL", "XXL", "Free"};
    private static final String[] MATERIALS = {"면", "폴리에스터", "나일론", "가죽", "플라스틱", "금속", "유리", "실리콘", "고무", "목재"};
    private static final String[] COUNTRIES = {"한국", "중국", "일본", "미국", "독일", "이탈리아", "프랑스", "베트남", "태국", "인도"};

    public static List<Product> createProducts(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    String brand = BRANDS[i % BRANDS.length];
                    String category = CATEGORIES[i % CATEGORIES.length];
                    BigDecimal regularPrice = BigDecimal.valueOf(10000L + (i % 90000));
                    int discountRate = random.nextInt(50); // 0-49% 할인
                    BigDecimal salePrice = regularPrice.multiply(BigDecimal.valueOf(100 - discountRate))
                            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

                    return Product.builder()
                            .name("테스트 상품 " + i)
                            .description("테스트 상품 설명 " + i + " - 이것은 성능 테스트를 위한 더미 데이터입니다. " +
                                    "상품 상세 정보가 포함되어 있으며, 배치 성능 테스트용으로 생성된 데이터입니다.")
                            .price(salePrice)
                            .sellerId((long) (i % 1000 + 1))
                            .pendingProductId((long) (i + 1))
                            .productCode("PRD" + String.format("%08d", i))
                            .brand(brand)
                            .manufacturer(brand + " 제조")
                            .category(category)
                            .subCategory(category + " 서브")
                            .productStatus(i % 10 == 0 ? "INACTIVE" : "ACTIVE")
                            .stockQuantity(random.nextInt(1000))
                            .regularPrice(regularPrice)
                            .discountRate(discountRate)
                            .salePrice(salePrice)
                            .weight((double) (random.nextInt(5000) + 100) / 100) // 1.0 ~ 50.0kg
                            .dimensions(String.format("%dx%dx%d",
                                    random.nextInt(100) + 10,
                                    random.nextInt(100) + 10,
                                    random.nextInt(100) + 10))
                            .color(COLORS[i % COLORS.length])
                            .size(SIZES[i % SIZES.length])
                            .material(MATERIALS[i % MATERIALS.length])
                            .originCountry(COUNTRIES[i % COUNTRIES.length])
                            .releaseDate(LocalDate.now().minusDays(random.nextInt(365)))
                            .expiryDate(LocalDate.now().plusDays(random.nextInt(365) + 365))
                            .isFeatured(i % 20 == 0)
                            .isNewArrival(i % 15 == 0)
                            .isBestSeller(i % 25 == 0)
                            .rating(BigDecimal.valueOf(random.nextDouble() * 5.0).setScale(2, java.math.RoundingMode.HALF_UP))
                            .reviewCount(random.nextInt(1000))
                            .viewCount((long) random.nextInt(10000))
                            .salesCount((long) random.nextInt(500))
                            .tags(String.format("태그%d,태그%d,태그%d", i % 10, (i + 1) % 10, (i + 2) % 10))
                            .keywords(String.format("키워드%d 키워드%d 키워드%d", i % 5, (i + 1) % 5, (i + 2) % 5))
                            .metaTitle("테스트 상품 " + i + " - 메타 타이틀")
                            .metaDescription("테스트 상품 " + i + "의 메타 설명입니다.")
                            .warrantyPeriod(random.nextInt(60) + 1) // 1~60개월
                            .shippingWeight((double) (random.nextInt(3000) + 100) / 100) // 1.0 ~ 30.0kg
                            .shippingFee(BigDecimal.valueOf(random.nextInt(5000)))
                            .minOrderQuantity(1)
                            .maxOrderQuantity(random.nextInt(50) + 1)
                            .lastRestockedAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                            .build();
                })
                .toList();
    }
}
