package com.f1v3.batch.repository.product;

import com.f1v3.batch.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}