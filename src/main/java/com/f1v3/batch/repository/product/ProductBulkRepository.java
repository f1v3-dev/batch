package com.f1v3.batch.repository.product;

import com.f1v3.batch.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductBulkRepository {

    private final DataSource dataSource;

    /**
     * JDBC Statement를 사용한 bulk insert
     */
    public void bulkInsertWithStatement(List<Product> products) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);

            for (Product product : products) {
                String sql = String.format(
                        "INSERT INTO products (name, description, price, seller_id, pending_product_id, created_at, updated_at) " +
                                "VALUES ('%s', '%s', %s, %d, %d, NOW(), NOW())",
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getSellerId(),
                        product.getPendingProductId()
                );
                statement.addBatch(sql);
            }

            statement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            System.err.println("Statement bulk insert 실패: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * JDBC PreparedStatement를 사용한 bulk insert
     */
    public void bulkInsertWithPreparedStatement(List<Product> products) {
        String sql = "INSERT INTO products (name, description, price, seller_id, pending_product_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (Product product : products) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                preparedStatement.setBigDecimal(3, product.getPrice());
                preparedStatement.setLong(4, product.getSellerId());
                preparedStatement.setLong(5, product.getPendingProductId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            System.err.println("PreparedStatement bulk insert 실패: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
