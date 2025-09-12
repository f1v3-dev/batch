package com.f1v3.batch.repository.product;

import com.f1v3.batch.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("DELETE FROM Product")
    void deleteAllProducts();
}