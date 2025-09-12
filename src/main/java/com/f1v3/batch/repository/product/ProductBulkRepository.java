package com.f1v3.batch.repository.product;

import com.f1v3.batch.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
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
                        "INSERT INTO products (name, description, price, seller_id, pending_product_id, " +
                                "product_code, brand, manufacturer, category, sub_category, product_status, " +
                                "stock_quantity, regular_price, discount_rate, sale_price, weight, dimensions, " +
                                "color, size, material, origin_country, release_date, expiry_date, " +
                                "is_featured, is_new_arrival, is_best_seller, rating, review_count, " +
                                "view_count, sales_count, tags, keywords, meta_title, meta_description, " +
                                "warranty_period, shipping_weight, shipping_fee, min_order_quantity, " +
                                "max_order_quantity, last_restocked_at, created_at, updated_at) " +
                                "VALUES ('%s', '%s', %s, %d, %d, '%s', '%s', '%s', '%s', '%s', '%s', " +
                                "%d, %s, %d, %s, %s, '%s', '%s', '%s', '%s', '%s', '%s', '%s', " +
                                "%b, %b, %b, %s, %d, %d, %d, '%s', '%s', '%s', '%s', " +
                                "%d, %s, %s, %d, %d, '%s', NOW(), NOW())",
                        escapeString(product.getName()),
                        escapeString(product.getDescription()),
                        product.getPrice(),
                        product.getSellerId(),
                        product.getPendingProductId(),
                        escapeString(product.getProductCode()),
                        escapeString(product.getBrand()),
                        escapeString(product.getManufacturer()),
                        escapeString(product.getCategory()),
                        escapeString(product.getSubCategory()),
                        escapeString(product.getProductStatus() != null ? product.getProductStatus() : "ACTIVE"),
                        product.getStockQuantity() != null ? product.getStockQuantity() : 0,
                        product.getRegularPrice() != null ? product.getRegularPrice() : product.getPrice(),
                        product.getDiscountRate() != null ? product.getDiscountRate() : 0,
                        product.getSalePrice() != null ? product.getSalePrice() : product.getPrice(),
                        product.getWeight() != null ? product.getWeight() : 0.0,
                        escapeString(product.getDimensions()),
                        escapeString(product.getColor()),
                        escapeString(product.getSize()),
                        escapeString(product.getMaterial()),
                        escapeString(product.getOriginCountry()),
                        product.getReleaseDate() != null ? product.getReleaseDate().toString() : "1970-01-01",
                        product.getExpiryDate() != null ? product.getExpiryDate().toString() : "2099-12-31",
                        product.getIsFeatured() != null ? product.getIsFeatured() : false,
                        product.getIsNewArrival() != null ? product.getIsNewArrival() : false,
                        product.getIsBestSeller() != null ? product.getIsBestSeller() : false,
                        product.getRating() != null ? product.getRating() : 0.0,
                        product.getReviewCount() != null ? product.getReviewCount() : 0,
                        product.getViewCount() != null ? product.getViewCount() : 0L,
                        product.getSalesCount() != null ? product.getSalesCount() : 0L,
                        escapeString(product.getTags()),
                        escapeString(product.getKeywords()),
                        escapeString(product.getMetaTitle()),
                        escapeString(product.getMetaDescription()),
                        product.getWarrantyPeriod() != null ? product.getWarrantyPeriod() : 0,
                        product.getShippingWeight() != null ? product.getShippingWeight() : 0.0,
                        product.getShippingFee() != null ? product.getShippingFee() : 0,
                        product.getMinOrderQuantity() != null ? product.getMinOrderQuantity() : 1,
                        product.getMaxOrderQuantity() != null ? product.getMaxOrderQuantity() : 999,
                        product.getLastRestockedAt() != null ? product.getLastRestockedAt().toString() : "1970-01-01 00:00:00"
                );
                statement.addBatch(sql);
            }

            statement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            log.error("Statement bulk insert 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Statement bulk insert 실패", e);
        }
    }

    /**
     * JDBC PreparedStatement를 사용한 bulk insert
     */
    public void bulkInsertWithPreparedStatement(List<Product> products) {
        String sql = "INSERT INTO products (name, description, price, seller_id, pending_product_id, " +
                "product_code, brand, manufacturer, category, sub_category, product_status, " +
                "stock_quantity, regular_price, discount_rate, sale_price, weight, dimensions, " +
                "color, size, material, origin_country, release_date, expiry_date, " +
                "is_featured, is_new_arrival, is_best_seller, rating, review_count, " +
                "view_count, sales_count, tags, keywords, meta_title, meta_description, " +
                "warranty_period, shipping_weight, shipping_fee, min_order_quantity, " +
                "max_order_quantity, last_restocked_at, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (Product product : products) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                preparedStatement.setBigDecimal(3, product.getPrice());
                preparedStatement.setLong(4, product.getSellerId());
                preparedStatement.setLong(5, product.getPendingProductId());
                preparedStatement.setString(6, product.getProductCode());
                preparedStatement.setString(7, product.getBrand());
                preparedStatement.setString(8, product.getManufacturer());
                preparedStatement.setString(9, product.getCategory());
                preparedStatement.setString(10, product.getSubCategory());
                preparedStatement.setString(11, product.getProductStatus() != null ? product.getProductStatus() : "ACTIVE");
                preparedStatement.setInt(12, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
                preparedStatement.setBigDecimal(13, product.getRegularPrice() != null ? product.getRegularPrice() : product.getPrice());
                preparedStatement.setInt(14, product.getDiscountRate() != null ? product.getDiscountRate() : 0);
                preparedStatement.setBigDecimal(15, product.getSalePrice() != null ? product.getSalePrice() : product.getPrice());
                preparedStatement.setDouble(16, product.getWeight() != null ? product.getWeight() : 0.0);
                preparedStatement.setString(17, product.getDimensions());
                preparedStatement.setString(18, product.getColor());
                preparedStatement.setString(19, product.getSize());
                preparedStatement.setString(20, product.getMaterial());
                preparedStatement.setString(21, product.getOriginCountry());
                preparedStatement.setObject(22, product.getReleaseDate());
                preparedStatement.setObject(23, product.getExpiryDate());
                preparedStatement.setBoolean(24, product.getIsFeatured() != null ? product.getIsFeatured() : false);
                preparedStatement.setBoolean(25, product.getIsNewArrival() != null ? product.getIsNewArrival() : false);
                preparedStatement.setBoolean(26, product.getIsBestSeller() != null ? product.getIsBestSeller() : false);
                preparedStatement.setBigDecimal(27, product.getRating() != null ? product.getRating() : java.math.BigDecimal.ZERO);
                preparedStatement.setInt(28, product.getReviewCount() != null ? product.getReviewCount() : 0);
                preparedStatement.setLong(29, product.getViewCount() != null ? product.getViewCount() : 0L);
                preparedStatement.setLong(30, product.getSalesCount() != null ? product.getSalesCount() : 0L);
                preparedStatement.setString(31, product.getTags());
                preparedStatement.setString(32, product.getKeywords());
                preparedStatement.setString(33, product.getMetaTitle());
                preparedStatement.setString(34, product.getMetaDescription());
                preparedStatement.setInt(35, product.getWarrantyPeriod() != null ? product.getWarrantyPeriod() : 0);
                preparedStatement.setDouble(36, product.getShippingWeight() != null ? product.getShippingWeight() : 0.0);
                preparedStatement.setBigDecimal(37, product.getShippingFee());
                preparedStatement.setInt(38, product.getMinOrderQuantity() != null ? product.getMinOrderQuantity() : 1);
                preparedStatement.setInt(39, product.getMaxOrderQuantity() != null ? product.getMaxOrderQuantity() : 999);
                preparedStatement.setObject(40, product.getLastRestockedAt());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            log.error("PreparedStatement bulk insert 실패: {}", e.getMessage(), e);
            throw new RuntimeException("PreparedStatement bulk insert 실패", e);
        }
    }

    private String escapeString(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "''");
    }
}
