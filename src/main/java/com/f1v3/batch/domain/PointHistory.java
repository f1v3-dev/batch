package com.f1v3.batch.domain;

import com.f1v3.batch.domain.enums.EarnReason;
import com.f1v3.batch.domain.enums.SpendReason;
import com.f1v3.batch.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistory {

    // todo: ID 생성 전략을 고민해야하는 이유
    //      1. 대량의 데이터를 삽입할 경우, 내부적으로 Auto Increment Lock이 발생한다. (innodb_autoinc_lock_mode)
    //      2. 반면, Auto Increment가 아닌 경우, Primary Key Index Table 순서는? 동일한 키 값을 가질 확률은?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter; // current point balance

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "earn_reason")
    private EarnReason earnReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "spend_reason")
    private SpendReason spendReason;

    @Column(length = 255)
    private String descript;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "batch_processed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean batchProcessed;

    public static class Builder {
        private Long memberId;
        private Integer points;
        private Integer balanceAfter;
        private TransactionType transactionType;
        private EarnReason earnReason;
        private SpendReason spendReason;
        private String descript;
        private LocalDateTime createdAt;

        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder points(Integer points) {
            this.points = points;
            return this;
        }

        public Builder balanceAfter(Integer balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder earnReason(EarnReason earnReason) {
            this.earnReason = earnReason;
            return this;
        }

        public Builder spendReason(SpendReason spendReason) {
            this.spendReason = spendReason;
            return this;
        }

        public Builder descript(String description) {
            this.descript = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PointHistory build() {
            // default value
            Boolean batchProcessed = false;

            return new PointHistory(
                    null,
                    memberId,
                    points,
                    balanceAfter,
                    transactionType,
                    earnReason,
                    spendReason,
                    descript,
                    createdAt != null ? createdAt : LocalDateTime.now(),
                    batchProcessed
            );
        }
    }
}