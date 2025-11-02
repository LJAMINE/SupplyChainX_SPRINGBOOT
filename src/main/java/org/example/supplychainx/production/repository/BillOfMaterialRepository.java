package org.example.supplychainx.production.repository;

import org.example.supplychainx.production.entity.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    List<BillOfMaterial> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
}