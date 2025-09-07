package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.PendingProduct;
import com.f1v3.batch.repository.pendingproduct.PendingProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LargeDataDummy {

    private final PendingProductRepository pendingProductRepository;

    /**
     * 100만개의 검수 대기 상품 생성
     */
    public void createMillionPendingProducts() {
        log.info("100만개 검수 대기 상품 생성 시작");
        long startTime = System.currentTimeMillis();

        int totalCount = 1_000_000;
        int batchSize = 10_000; // 10,000개씩 나누어서 생성

        for (int batch = 0; batch < totalCount / batchSize; batch++) {
            List<PendingProduct> products = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                int productNum = batch * batchSize + i + 1;

                PendingProduct product = PendingProduct.builder()
                        .name("상품_" + productNum)
                        .description("상품 설명 " + productNum + " - 대량 테스트 데이터")
                        .price(new BigDecimal(String.valueOf(1000 + (productNum % 100000))))
                        .sellerId((long) (2 + (productNum % 2))) // seller1(2L) 또는 seller2(3L)
                        .build();

                products.add(product);
            }

            // JPA saveAll 사용 - 여기서 성능 문제 발생 예상
            long batchStartTime = System.currentTimeMillis();
            pendingProductRepository.saveAll(products);
            long batchEndTime = System.currentTimeMillis();

            log.info("배치 {}/{} 완료 - {}개 상품 저장, 소요시간: {}ms",
                    batch + 1, totalCount / batchSize, batchSize, batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        log.info("100만개 검수 대기 상품 생성 완료 - 총 소요시간: {}초", (endTime - startTime) / 1000);
    }

    /**
     * 테스트용 소량 데이터 생성 (10,000개)
     */
    public void createTestPendingProducts() {
        log.info("테스트용 10,000개 검수 대기 상품 생성 시작");
        long startTime = System.currentTimeMillis();

        List<PendingProduct> products = new ArrayList<>();

        for (int i = 1; i <= 10_000; i++) {
            PendingProduct product = PendingProduct.builder()
                    .name("테스트상품_" + i)
                    .description("테스트 상품 설명 " + i)
                    .price(new BigDecimal(String.valueOf(1000 + i)))
                    .sellerId((long) (2 + (i % 2)))
                    .build();

            products.add(product);
        }

        // JPA saveAll 사용
        pendingProductRepository.saveAll(products);

        long endTime = System.currentTimeMillis();
        log.info("테스트용 10,000개 검수 대기 상품 생성 완료 - 소요시간: {}ms", endTime - startTime);
    }

    /**
     * 200만개의 다양한 상태 검수 상품 생성
     * - APPROVED: 100만개
     * - PENDING: 50만개
     * - REJECTED: 50만개
     */
    public void createTwoMillionMixedStatusProducts() {
        log.info("200만개 다양한 상태 검수 상품 생성 시작");
        long startTime = System.currentTimeMillis();

        int totalCount = 2_000_000;
        int batchSize = 10_000;

        // 상태별 개수 정의
        int approvedCount = 1_000_000;  // 100만개
        int pendingCount = 500_000;     // 50만개
        int rejectedCount = 500_000;    // 50만개

        int processedCount = 0;

        for (int batch = 0; batch < totalCount / batchSize; batch++) {
            List<PendingProduct> products = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                int productNum = batch * batchSize + i + 1;
                processedCount++;

                PendingProduct product = PendingProduct.builder()
                        .name("상품_" + productNum)
                        .description("상품 설명 " + productNum + " - 대량 테스트 데이터 (혼합 상태)")
                        .price(new BigDecimal(String.valueOf(1000 + (productNum % 100000))))
                        .sellerId((long) (2 + (productNum % 10))) // seller2(2L)부터 seller11(11L)까지 다양하게
                        .build();

                // 상태 설정 로직
                if (processedCount <= approvedCount) {
                    // 처음 100만개는 APPROVED
                    product.approve(1L); // 관리자 ID 1L로 승인
                } else if (processedCount <= approvedCount + pendingCount) {
                    // 다음 50만개는 PENDING (기본 상태이므로 별도 처리 불필요)
                    // PendingProduct의 기본 상태가 PENDING이므로 추가 작업 없음
                } else {
                    // 마지막 50만개는 REJECTED
                    String[] rejectionReasons = {
                        "부적절한 상품명",
                        "가격 정보 부정확",
                        "상품 설명 불충분",
                        "정책 위반",
                        "중복 상품"
                    };
                    String reason = rejectionReasons[productNum % rejectionReasons.length];
                    product.reject(1L, reason); // 관리자 ID 1L로 거절
                }

                products.add(product);
            }

            // JPA saveAll 사용
            long batchStartTime = System.currentTimeMillis();
            pendingProductRepository.saveAll(products);
            long batchEndTime = System.currentTimeMillis();

            log.info("배치 {}/{} 완료 - {}개 상품 저장 (처리된 총 개수: {}), 소요시간: {}ms",
                    batch + 1, totalCount / batchSize, batchSize, processedCount, batchEndTime - batchStartTime);
        }

        long endTime = System.currentTimeMillis();
        log.info("200만개 다양한 상태 검수 상품 생성 완료 - 총 소요시간: {}초", (endTime - startTime) / 1000);
        log.info("생성된 데이터 현황: APPROVED={}만개, PENDING={}만개, REJECTED={}만개",
                approvedCount/10000, pendingCount/10000, rejectedCount/10000);
    }
}
