package com.f1v3.batch.repository;

import com.f1v3.batch.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * JdbcTemplate batchUpdate를 사용한 BULK INSERT
     */
    public void batchInsert(List<Product> products) {
        String sql = """
                INSERT INTO products (name, description, price, seller_id, pending_product_id, created_at) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, products, products.size(),
                (PreparedStatement ps, Product product) -> {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getDescription());
                    ps.setBigDecimal(3, product.getPrice());
                    ps.setLong(4, product.getSellerId());
                    ps.setLong(5, product.getPendingProductId());
                    ps.setTimestamp(6, Timestamp.valueOf(product.getCreatedAt()));
                });
    }

    /**
     * 청크 단위로 나누어서 batchUpdate 실행
     */
    public void chunkedBatchInsert(List<Product> products, int chunkSize) {
        String sql = """
                INSERT INTO products (name, description, price, seller_id, pending_product_id, created_at) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        int totalSize = products.size();
        for (int i = 0; i < totalSize; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, totalSize);
            List<Product> chunk = products.subList(i, endIndex);

            long chunkStartTime = System.currentTimeMillis();
            jdbcTemplate.batchUpdate(sql, chunk, chunk.size(),
                    (PreparedStatement ps, Product product) -> {
                        ps.setString(1, product.getName());
                        ps.setString(2, product.getDescription());
                        ps.setBigDecimal(3, product.getPrice());
                        ps.setLong(4, product.getSellerId());
                        ps.setLong(5, product.getPendingProductId());
                        ps.setTimestamp(6, Timestamp.valueOf(product.getCreatedAt()));
                    });
            long chunkEndTime = System.currentTimeMillis();

            log.info("JDBC 청크 {}/{} 완료 - {}개 상품 저장, 소요시간: {}ms",
                    (i / chunkSize) + 1, (totalSize + chunkSize - 1) / chunkSize,
                    chunk.size(), chunkEndTime - chunkStartTime);
        }
    }
}
