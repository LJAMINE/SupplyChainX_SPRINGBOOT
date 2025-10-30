package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    Page<Supplier>findByNameContainingIgnoreCase(String name, Pageable pageable);
  }
