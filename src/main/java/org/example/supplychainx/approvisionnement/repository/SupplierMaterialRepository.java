package org.example.supplychainx.approvisionnement.repository;

import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.entity.SupplierMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierMaterialRepository  extends JpaRepository<SupplierMaterial, Long> {

    Page<SupplierMaterial> findBySupplierId(Long supplierId, Pageable pageable);
    Page<SupplierMaterial> findByRawMaterial_Id(Long rawmaterialId, Pageable pageable);
    boolean existsBySupplierIdAndRawMaterialId(Long supplierId, Long rawMaterialId);





}
