package org.example.supplychainx.production.repository;

import org.example.supplychainx.production.entity.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Page<ProductionOrder> findAll(Pageable pageable);
    boolean existsByProductId(Long productId);
}