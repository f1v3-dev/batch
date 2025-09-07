package com.f1v3.batch.repository.pendingproduct;

import com.f1v3.batch.domain.PendingProduct;
import com.f1v3.batch.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PendingProductRepository extends JpaRepository<PendingProduct, Long> {

    @Query("""
             SELECT p FROM PendingProduct p
             WHERE p.name LIKE CONCAT('%', :keyword, '%')
             ORDER BY p.id DESC
            """
    )
    List<PendingProduct> findByNameAndDescription(@Param("keyword") String keyword);


    @Query(value = """
            SELECT *
            FROM pending_products p
            WHERE MATCH(p.name) AGAINST(?1 IN BOOLEAN MODE)
            """, nativeQuery = true)
    List<PendingProduct> findByNameAndDescriptionV2(String keyword);


    List<PendingProduct> findByStatus(ProductStatus status);
}
