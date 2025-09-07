package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.PendingProduct;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class PendingProductDummy {

    private PendingProductDummy() {
        throw new IllegalStateException();
    }

    // 상품 카테고리별 데이터 - 가중치 적용을 위해 분리
    private static final String[] POPULAR_BRANDS = {
            "Samsung", "Apple", "Nike", "Adidas", "Zara", "H&M", "Uniqlo"  // 70% 비중
    };

    private static final String[] PREMIUM_BRANDS = {
            "Chanel", "Dior", "Gucci", "Louis Vuitton", "Hermès", "Prada", "Burberry"  // 20% 비중
    };

    private static final String[] NICHE_BRANDS = {
            "Muji", "Bose", "Canon", "Sony", "이케아", "한샘", "교보문고", "민음사", "CJ", "농심"  // 10% 비중
    };

    private static final String[] TECH_KEYWORDS = {
            "스마트", "AI", "무선", "블루투스", "4K", "HD", "고속충전", "방수", "노이즈캔슬링",
            "터치스크린", "얼굴인식", "음성인식", "IoT", "스마트홈", "웨어러블"
    };

    private static final String[] FASHION_KEYWORDS = {
            "빈티지", "미니멀", "오버사이즈", "슬림핏", "크롭", "와이드", "하이웨이스트",
            "로우라이즈", "오프숄더", "백리스", "시스루", "레이어드", "매치", "믹스앤매치"
    };

    private static final String[] LIFESTYLE_KEYWORDS = {
            "친환경", "지속가능", "비건", "유기농", "미니멀라이프", "제로웨이스트", "업사이클링",
            "핸드메이드", "아티산", "로컬", "페어트레이드", "슬로우라이프", "웰빙", "힐링"
    };

    // 실제 상품 설명에서 자주 나오는 긴 문장들
    private static final String[] DETAILED_DESCRIPTIONS = {
            "엄선된 프리미엄 소재로 제작되어 뛰어난 내구성과 편안한 착용감을 자랑합니다. 일상생활은 물론 특별한 순간까지 완벽하게 어울리는 타임리스한 디자인입니다.",
            "혁신적인 기술력과 세련된 디자인이 만나 탄생한 차세대 제품으로, 사용자의 라이프스타일을 한층 더 스마트하고 편리하게 만들어드립니다.",
            "전 세계 패션 트렌드를 선도하는 브랜드의 시그니처 아이템으로, 독창적인 컬러와 실루엣이 돋보이는 한정 에디션입니다.",
            "까다로운 품질 검증 과정을 거쳐 완성된 프리미엄 제품으로, 오랜 시간 변치 않는 가치를 선사합니다. 선물용으로도 완벽한 선택입니다.",
            "인체공학적 설계와 최첨단 소재 기술이 결합되어 최상의 사용자 경험을 제공합니다. 전문가들이 인정한 혁신적인 기능성을 경험해보세요.",
            "지속가능한 환경을 위한 친환경 제조 공정으로 만들어진 제품입니다. 자연에서 영감을 받은 디자인과 우수한 기능성을 동시에 만족시킵니다."
    };

    private static final String[] CATEGORIES = {
            "패션의류", "신발", "가방", "액세서리", "화장품", "향수", "시계", "주얼리",
            "스마트폰", "태블릿", "노트북", "컴퓨터", "카메라", "이어폰", "스피커", "게임기",
            "가구", "침구", "조명", "수납", "인테리어", "주방용품", "생활용품", "청소용품",
            "운동화", "운동복", "헬스기구", "자전거", "골프", "수영", "요가", "등산",
            "소설", "에세이", "자기계발", "요리책", "여행", "취미", "건강", "육아",
            "간식", "음료", "건강식품", "차", "커피", "과일", "견과류", "유기농"
    };

    private static final String[] COLORS = {
            "블랙", "화이트", "그레이", "네이비", "브라운", "베이지", "카키", "레드", "핑크", "오렌지",
            "옐로우", "그린", "블루", "퍼플", "골드", "실버", "로즈골드", "민트", "라벤더", "코랄"
    };

    private static final String[] MATERIALS = {
            "면", "폴리에스터", "울", "리넨", "실크", "가죽", "스웨이드", "데님", "니트", "메시",
            "플라스틱", "금속", "유리", "세라믹", "목재", "대나무", "스테인리스", "알루미늄", "티타늄", "카본"
    };

    private static final String[] SIZES = {
            "XS", "S", "M", "L", "XL", "XXL", "Free Size", "95", "100", "105", "110", "230", "240", "250", "260", "270"
    };

    private static final String[] FEATURES = {
            "방수", "방풍", "통기성", "신축성", "항균", "UV차단", "흡습속건", "보온", "쿨링", "논슬립",
            "무선", "블루투스", "WiFi", "4K", "HD", "고화질", "저전력", "고속충전", "무소음", "에코프렌들리",
            "유기농", "무첨가", "비건", "글루텐프리", "저칼로리", "고단백", "비타민", "미네랄", "프로바이오틱스", "천연"
    };

    private static final String[] PRODUCT_TYPES = {
            "티셔츠", "셔츠", "후드티", "맨투맨", "자켓", "코트", "청바지", "슬랙스", "스커트", "원피스",
            "운동화", "구두", "부츠", "샌들", "슬리퍼", "백팩", "숄더백", "토트백", "크로스백", "지갑",
            "스마트폰", "케이스", "이어폰", "충전기", "보조배터리", "스피커", "키보드", "마우스", "모니터", "웹캠",
            "소파", "침대", "옷장", "책상", "의자", "테이블", "수납함", "거울", "시계", "램프",
            "샴푸", "린스", "바디워시", "로션", "크림", "세럼", "마스크팩", "클렌징폼", "토너", "선크림",
            "소설", "에세이", "만화", "잡지", "요리책", "여행서", "자기계발서", "건강서", "육아서", "취미서",
            "과자", "초콜릿", "사탕", "젤리", "음료수", "차", "커피", "견과류", "과일", "건강식품"
    };

    private static final Random RANDOM = new Random();

    /**
     * 가중치 기반 브랜드 선택
     */
    private static String selectBrandWithWeight() {
        int rand = RANDOM.nextInt(100);
        if (rand < 70) {
            return POPULAR_BRANDS[RANDOM.nextInt(POPULAR_BRANDS.length)];
        } else if (rand < 90) {
            return PREMIUM_BRANDS[RANDOM.nextInt(PREMIUM_BRANDS.length)];
        } else {
            return NICHE_BRANDS[RANDOM.nextInt(NICHE_BRANDS.length)];
        }
    }

    /**
     * 다양한 상품 설명 생성
     */
    private static String generateProductDescription(int index) {
        String category = CATEGORIES[index % CATEGORIES.length];
        String material = MATERIALS[index % MATERIALS.length];
        String size = SIZES[index % SIZES.length];
        String feature1 = FEATURES[index % FEATURES.length];
        String feature2 = FEATURES[(index + 1) % FEATURES.length];
        String feature3 = FEATURES[(index + 2) % FEATURES.length];

        String[] seasons = {"봄", "여름", "가을", "겨울", "사계절"};
        String season = seasons[index % seasons.length];

        String[] ages = {"10대", "20대", "30대", "40대", "50대", "전연령"};
        String age = ages[index % ages.length];

        String[] occasions = {"데일리", "비즈니스", "캐주얼", "포멀", "스포츠", "아웃도어", "홈웨어", "파티"};
        String occasion = occasions[index % occasions.length];

        // 다양한 설명 패턴
        int pattern = index % 6;
        return switch (pattern) {
            case 0 -> String.format("%s 전문 브랜드의 %s 제품입니다. %s 소재로 제작되어 %s, %s 기능을 제공합니다. " +
                            "%s용으로 제작되었으며, %s에게 특히 인기가 높습니다. 사이즈: %s",
                    category, feature1, material, feature2, feature3, occasion, age, size);

            case 1 -> String.format("프리미엄 %s 라인의 신제품입니다. %s 시즌에 완벽한 %s 제품으로, " +
                            "%s 소재의 고급스러운 마감이 특징입니다. %s, %s 기능으로 실용성까지 갖췄습니다.",
                    category, season, feature1, material, feature2, feature3);

            case 2 -> String.format("[한정 수량] %s 컬렉션의 베스트셀러입니다. %s를 위한 %s 디자인으로 " +
                            "%s과 %s을 동시에 만족시킵니다. %s 소재 사용으로 내구성이 뛰어납니다.",
                    category, age, occasion, feature1, feature2, material);

            case 3 -> String.format("트렌디한 %s 아이템으로 %s 라이프스타일에 완벽합니다. " +
                            "%s, %s, %s 등 다양한 기능성을 갖고 있어 실용적입니다. " +
                            "고품질 %s 소재로 제작되어 오래 사용 가능합니다.",
                    category, occasion, feature1, feature2, feature3, material);

            case 4 -> String.format("인기 %s 브랜드의 시그니처 제품입니다. %s 시즌 필수 아이템으로 " +
                            "%s 기능이 뛰어나며, %s과 %s을 겸비했습니다. " +
                            "%s에게 추천하는 %s 전용 제품입니다.",
                    category, season, feature1, feature2, feature3, age, occasion);

            case 5 -> String.format("[리뷰 평점 4.8★] %s 분야 베스트 아이템입니다. " +
                            "%s 소재의 프리미엄 품질과 %s, %s 기능성이 특징입니다. " +
                            "%s용으로 디자인되어 %s 고객들에게 최적화되어 있습니다. 사이즈: %s",
                    category, material, feature1, feature2, occasion, age, size);

            default -> String.format("%s 카테고리의 인기 상품입니다. %s 소재로 제작되어 %s 기능을 제공합니다.",
                    category, material, feature1);
        };
    }

    /**
     * 다양한 상품명 생성 - 불규칙한 패턴
     */
    private static String generateRealisticProductName(int index) {
        String brand = selectBrandWithWeight();
        String category = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];

        // 브랜드별로 다른 패턴 적용
        if (brand.equals("Apple") || brand.equals("Samsung")) {
            String[] techSuffixes = {"Pro", "Max", "Ultra", "Plus", "Mini", "Air", "Studio"};
            String suffix = techSuffixes[RANDOM.nextInt(techSuffixes.length)];
            String model = "Series " + (RANDOM.nextInt(15) + 1);
            return String.format("%s %s %s %s", brand, category, model, suffix);
        } else if (isPremiumBrand(brand)) {
            String[] luxuryTerms = {"컬렉션", "에디션", "시그니처", "익스클루시브", "리미티드"};
            String term = luxuryTerms[RANDOM.nextInt(luxuryTerms.length)];
            return String.format("[%s] %s %s %s", brand, term, category, generateSeasonYear());
        } else {
            // 일반 브랜드는 단순한 패턴
            String[] colors = {"블랙", "화이트", "네이비", "베이지", "그레이"};
            String color = colors[RANDOM.nextInt(colors.length)];
            return String.format("%s %s %s", brand, color, category);
        }
    }

    private static String generateRealisticDescription(int index, String productName) {
        StringBuilder description = new StringBuilder();

        // 기본 설명 추가
        description.append(DETAILED_DESCRIPTIONS[RANDOM.nextInt(DETAILED_DESCRIPTIONS.length)]);
        description.append(" ");

        // 브랜드 정보 추가 (30% 확률)
        if (RANDOM.nextInt(10) < 3) {
            description.append("브랜드 창립 이래 지켜온 장인정신과 혁신적인 기술이 담긴 제품입니다. ");
        }

        // 기술적 특징 추가 (카테고리에 따라)
        if (productName.contains("스마트") || productName.contains("테크")) {
            String techKeyword = TECH_KEYWORDS[RANDOM.nextInt(TECH_KEYWORDS.length)];
            description.append(String.format("최신 %s 기술이 적용되어 ", techKeyword));
            description.append("사용자 편의성을 극대화했습니다. ");
        }

        // 패션 관련 키워드 (패션 아이템인 경우)
        if (productName.contains("패션") || productName.contains("의류") || productName.contains("가방")) {
            String fashionKeyword = FASHION_KEYWORDS[RANDOM.nextInt(FASHION_KEYWORDS.length)];
            description.append(String.format("트렌디한 %s 스타일로 ", fashionKeyword));
            description.append("어떤 코디에도 완벽하게 어울립니다. ");
        }

        // 라이프스타일 키워드 (20% 확률로 추가)
        if (RANDOM.nextInt(10) < 2) {
            String lifestyleKeyword = LIFESTYLE_KEYWORDS[RANDOM.nextInt(LIFESTYLE_KEYWORDS.length)];
            description.append(String.format("%s을 추구하는 현대인들에게 ", lifestyleKeyword));
            description.append("완벽한 선택이 될 것입니다. ");
        }

        // 특별한 기능이나 이벤트 (10% 확률)
        if (RANDOM.nextInt(10) < 1) {
            String[] specialFeatures = {
                    "한정 수량으�� 제작되어 희소성이 높습니���.",
                    "유명 디자이너와의 콜라보레이션 제품입니다.",
                    "연예인 협찬 제품으로 화제가 된 아이템입니다.",
                    "SNS에서 인플루언서들이 극찬한 화제의 제품입니다.",
                    "해외 직수입으로 국내에서는 만나기 어려운 제품입니다."
            };
            description.append(specialFeatures[RANDOM.nextInt(specialFeatures.length)]);
            description.append(" ");
        }

        // 리뷰나 평점 정보 (40% 확률)
        if (RANDOM.nextInt(10) < 4) {
            double rating = 4.0 + (RANDOM.nextDouble() * 1.0); // 4.0~5.0
            int reviewCount = 100 + RANDOM.nextInt(9900); // 100~10000
            description.append(String.format("고객 만족도 %.1f★ (리뷰 %,d개) ", rating, reviewCount));
        }

        return description.toString().trim();
    }

    private static boolean isPremiumBrand(String brand) {
        for (String premiumBrand : PREMIUM_BRANDS) {
            if (premiumBrand.equals(brand)) {
                return true;
            }
        }
        return false;
    }

    private static String generateSeasonYear() {
        String[] seasons = {"SS", "FW", "Spring", "Summer", "Fall", "Winter"};
        int year = 2020 + RANDOM.nextInt(6); // 2020-2025
        return seasons[RANDOM.nextInt(seasons.length)] + year;
    }

    public static List<PendingProduct> createPendingProducts() {
        log.info("100만개 검수 대기 상품 생성 시작 (현실적인 데이터)");

        List<PendingProduct> allProducts = new ArrayList<>();
        int totalCount = 1_000_000;

        for (int i = 1; i <= totalCount; i++) {
            String productName = generateRealisticProductName(i);

            PendingProduct product = PendingProduct.builder()
                    .name(productName)
                    .description(generateRealisticDescription(i, productName))
                    .price(generateRealisticPrice(productName))
                    .sellerId((long) (2 + (i % 2))) // seller1(2L) 또는 seller2(3L)
                    .build();

            allProducts.add(product);
        }

        log.info("100만개 검수 대기 상품 생성 완료");
        return allProducts;
    }

    public static List<PendingProduct> createTwoMillionMixedStatusProducts() {
        log.info("100만개 다양한 상태 검수 상품 생성 시작 (현실적인 데이터)");

        List<PendingProduct> allProducts = new ArrayList<>();
        int totalCount = 1_000_000;

        int approvedCount = 600_000;
        int pendingCount = 200_000;
        int rejectedCount = 200_000;

        for (int i = 1; i <= totalCount; i++) {
            String productName = generateRealisticProductName(i);

            PendingProduct product = PendingProduct.builder()
                    .name(productName)
                    .description(generateRealisticDescription(i, productName))
                    .price(generateRealisticPrice(productName))
                    .sellerId((long) (2 + (i % 10))) // seller2(2L)부터 seller11(11L)까지 다양하게
                    .build();

            // 상태 설정 로직
            if (i <= approvedCount) {
                product.approve(1L);
            } else if (i <= approvedCount + pendingCount) {
                // PENDING 상태 유지
            } else {
                String[] rejectionReasons = {
                        "부적절한 상품명",
                        "가격 정보 부정확",
                        "상품 설명 불충분",
                        "정책 위반",
                        "중복 상품"
                };
                String reason = rejectionReasons[i % rejectionReasons.length];
                product.reject(1L, reason);
            }

            allProducts.add(product);
        }

        log.info("생성된 데이터 현황: APPROVED={}만개, PENDING={}만개, REJECTED={}만개",
                approvedCount / 10000, pendingCount / 10000, rejectedCount / 10000);

        return allProducts;
    }

    /**
     * 브랜���와 카테고리에 따른 현실적인 가격 생성
     */
    private static BigDecimal generateRealisticPrice(String productName) {
        // 프리미엄 브랜드는 높은 가격
        for (String premiumBrand : PREMIUM_BRANDS) {
            if (productName.contains(premiumBrand)) {
                int price = 100000 + RANDOM.nextInt(900000); // 10만~100만원
                return new BigDecimal(price);
            }
        }

        // 테크 제품은 중간~높은 가격
        if (productName.contains("스마트폰") || productName.contains("노트북") || productName.contains("카메���")) {
            int price = 50000 + RANDOM.nextInt(200000); // 5만~25만원
            return new BigDecimal(price);
        }

        // 일반 제품은 낮은~중간 가격
        int price = 5000 + RANDOM.nextInt(45000); // 5천~5만원
        return new BigDecimal(price);
    }
}
