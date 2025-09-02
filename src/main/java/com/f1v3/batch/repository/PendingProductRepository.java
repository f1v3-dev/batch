package com.f1v3.batch.repository;

import com.f1v3.batch.domain.PendingProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingProductRepository extends JpaRepository<PendingProduct, Long> {
}
