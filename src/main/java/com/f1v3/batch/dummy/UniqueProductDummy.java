package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.PendingProduct;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class UniqueProductDummy {

    private UniqueProductDummy() {
        throw new IllegalStateException();
    }

    // 브랜드 (25개)
    private static final String[] BRANDS = {
            "Samsung", "Apple", "Nike", "Adidas", "Zara", "H&M", "Uniqlo", "Chanel", "Dior", "Gucci",
            "Louis Vuitton", "Hermès", "Prada", "Burberry", "Muji", "Bose", "Canon", "Sony", "이케아",
            "한샘", "교보문고", "민음사", "CJ", "농심", "무인양품"
    };

    // 카테고리 (37개)
    private static final String[] CATEGORIES = {
            "패션의류", "신발", "가방", "액세서리", "화장품", "향수", "시계", "주얼리",
            "스마트폰", "태블릿", "노트북", "컴퓨터", "카메라", "이어폰", "스피커", "게임기",
            "가구", "침구", "조명", "수납", "인테리어", "주방용품", "생활용품", "청소용품",
            "운동화", "운동복", "헬스기구", "자전거", "골프", "수영", "요가", "등산",
            "소설", "에세이", "자기계발", "간식", "음료"
    };

    // 색상/특성 (70개)
    private static final String[] ATTRIBUTES = {
            "블랙", "화이트", "그레이", "네이비", "브라운", "베이지", "카키", "레드", "핑크", "오렌지",
            "옐로우", "그린", "블루", "퍼플", "골드", "실버", "로즈골드", "민트", "라벤더", "코랄",
            "프로", "맥스", "울트라", "플러스", "미니", "에어", "스튜디오", "라이트", "스마트", "무선",
            "방수", "항균", "친환경", "프리미엄", "럭셔리", "클래식", "모던", "빈티지", "미니멀", "오버사이즈",
            "슬림", "와이드", "크롭", "롱", "숏", "하이", "로우", "소프트", "하드", "매트",
            "글로시", "시어", "옵틱", "투명", "불투명", "메탈릭", "펄", "글리터", "패브릭", "레더",
            "우드", "글래스", "세라믹", "실리콘", "스틸", "알루미늄", "카본", "티타늄", "플라스틱", "러버"
    };

    public static List<PendingProduct> createExactly64625UniqueProducts() {

        List<PendingProduct> products = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        int count = 0;
         int TARGET_COUNT = 64625;

        // 브랜드 × 카테고리 × 속성 순서로 조합 생성
        for (int brandIndex = 0; brandIndex < BRANDS.length && count < TARGET_COUNT; brandIndex++) {
            for (int categoryIndex = 0; categoryIndex < CATEGORIES.length && count < TARGET_COUNT; categoryIndex++) {
                for (int attributeIndex = 0; attributeIndex < ATTRIBUTES.length && count < TARGET_COUNT; attributeIndex++) {

                    String brand = BRANDS[brandIndex];
                    String category = CATEGORIES[categoryIndex];
                    String attribute = ATTRIBUTES[attributeIndex];

                    // 상품명 생성 (브랜드 + 속성 + 카테고리 형태)
                    String productName = String.format("%s %s %s", brand, attribute, category);

                    // 중복 체크 (혹시 모를 상황을 대비)
                    if (usedNames.contains(productName)) {
                        log.warn("중복된 상품명 발견: {}", productName);
                        continue;
                    }

                    usedNames.add(productName);
                    count++;

                    // 상품 생성
                    PendingProduct product = PendingProduct.builder()
                            .name(productName)
                            .description(generateDescription(brand, category, attribute, count))
                            .price(generatePrice(brand, category, count))
                            .sellerId(generateSellerId(count))
                            .build();

                    products.add(product);

                    // 진행상황 로그 (매 10,000개마다)
                    if (count % 10000 == 0) {
                        log.info("진행상황: {}/64,625 ({:.1f}%)", count, (count / 64625.0) * 100);
                    }
                }
            }
        }

        log.info("64,625개의 고유한 상품 생성 완료. 실제 생성된 수: {}", products.size());
        log.info("고유한 상품명 수: {}", usedNames.size());

        // 검증
        if (products.size() != TARGET_COUNT) {
            log.error("예상된 수량과 다릅니다. 예상: {}, 실제: {}", TARGET_COUNT, products.size());
        }

        return products;
    }

    /**
     * 상품 설명 생성
     */
    private static String generateDescription(String brand, String category, String attribute, int index) {
        String[] baseDescriptions = {
            "프리미엄 품질의 %s 제품입니다. %s 기능이 특징이며, %s 분야의 혁신적인 솔루션을 제공합니다.",
            "%s 브랜드의 시그니처 %s 아이템입니다. %s 디자인으로 뛰어난 실용성과 스타일을 겸비했습니다.",
            "엄선된 소재로 제작된 %s의 %s 제품입니다. %s 특성으로 일상생활에서 완벽한 만족감을 선사합니다.",
            "혁신적인 기술력이 담긴 %s %s 제품입니다. %s 기능으로 사용자의 라이프스타일을 업그레이드합니다.",
            "%s에서 출시한 프리미엄 %s 라인입니다. %s 특징을 가진 고품질 제품으로 오랜 시간 사용 가능합니다."
        };

        String template = baseDescriptions[index % baseDescriptions.length];
        return String.format(template, brand, category, attribute);
    }

    /**
     * 브랜드와 카테고리에 따른 가격 생성
     */
    private static BigDecimal generatePrice(String brand, String category, int index) {
        // 브랜드별 기본 가격 배수
        int brandMultiplier = 1;
        if (isPremiumBrand(brand)) {
            brandMultiplier = 5; // 프리미엄 브랜드는 5배
        } else if (isPopularBrand(brand)) {
            brandMultiplier = 2; // 인기 브랜드는 2배
        }

        // 카테고리별 기본 가격
        int basePrice = getCategoryBasePrice(category);

        // 최종 가격 계산 (약간의 변동성 추가)
        int variation = (index % 10) + 1; // 1~10
        int finalPrice = basePrice * brandMultiplier * variation;

        return new BigDecimal(finalPrice);
    }

    private static boolean isPremiumBrand(String brand) {
        return brand.equals("Chanel") || brand.equals("Dior") || brand.equals("Gucci") ||
               brand.equals("Louis Vuitton") || brand.equals("Hermès") || brand.equals("Prada") ||
               brand.equals("Burberry");
    }

    private static boolean isPopularBrand(String brand) {
        return brand.equals("Samsung") || brand.equals("Apple") || brand.equals("Nike") ||
               brand.equals("Adidas") || brand.equals("Zara") || brand.equals("H&M") ||
               brand.equals("Uniqlo");
    }

    private static int getCategoryBasePrice(String category) {
        return switch (category) {
            case "스마트폰", "태블릿", "노트북", "컴퓨터" -> 100000;
            case "시계", "주얼리", "향수" -> 50000;
            case "가구", "침구", "조명" -> 30000;
            case "패션의류", "신발", "가방" -> 20000;
            case "화장품", "액세서리" -> 15000;
            case "운동화", "운동복", "헬스기구" -> 10000;
            case "간식", "음료", "생활용품" -> 5000;
            default -> 0;
        };
    }

    /**
     * 판매자 ID 생성 (2L, 3L, 4L 순환)
     */
    private static Long generateSellerId(int index) {
        return (long) (2 + (index % 3));
    }

    /**
     * 조합 가능한 총 개수 계산 메서드
     */
    public static int calculateMaxCombinations() {
        return BRANDS.length * CATEGORIES.length * ATTRIBUTES.length;
    }
}
