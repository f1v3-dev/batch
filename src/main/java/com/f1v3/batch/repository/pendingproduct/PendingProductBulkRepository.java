package com.f1v3.batch.repository.pendingproduct;

import com.f1v3.batch.domain.PendingProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PendingProductBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertWithPreparedStatement(List<PendingProduct> products) {
        String sql = """
            INSERT INTO pending_products (name, description, price, seller_id, status, submitted_at, reviewed_at, reviewed_by, rejection_reason, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            """;

        jdbcTemplate.batchUpdate(sql, products, products.size(), (PreparedStatement ps, PendingProduct product) -> {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setLong(4, product.getSellerId());
            ps.setString(5, product.getStatus().name());
            ps.setTimestamp(6, Timestamp.valueOf(product.getSubmittedAt()));

            // reviewed_at, reviewed_by, rejection_reason은 null일 수 있음
            if (product.getReviewedAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(product.getReviewedAt()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }

            if (product.getReviewedBy() != null) {
                ps.setLong(8, product.getReviewedBy());
            } else {
                ps.setNull(8, java.sql.Types.BIGINT);
            }

            ps.setString(9, product.getRejectionReason());
        });
    }
}
