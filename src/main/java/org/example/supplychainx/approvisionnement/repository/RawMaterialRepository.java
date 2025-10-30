package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    Page<RawMaterial> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
}