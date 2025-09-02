package com.f1v3.batch.domain;

import com.f1v3.batch.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "pending_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingProduct extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Builder
    public PendingProduct(String name, String description, BigDecimal price, Long sellerId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.status = ProductStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public void approve(Long adminId) {
        this.status = ProductStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = adminId; // 검수자 ID
    }

    public void reject(Long adminId, String reason) {
        this.status = ProductStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = adminId;
        this.rejectionReason = reason;
    }
}
