package org.example.supplychainx.approvisionnement.service.interf;

import org.example.supplychainx.approvisionnement.dto.*;
import org.example.supplychainx.approvisionnement.entity.SupplierMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierMaterialService {
    Page<SupplierMaterialResponseDto> list(String s, Pageable pageable);
    Page<SupplierMaterialResponseDto> listBySupplierId(Long supplierId, Pageable pageable);
    Page<SupplierMaterialResponseDto> listByRawMaterialId(Long rawmaterialId, Pageable pageable);
     SupplierMaterialResponseDto get(Long id);
    SupplierMaterialResponseDto create (SupplierMaterialRequestDto dto );
    SupplierMaterialResponseDto update(Long id, SupplierMaterialRequestDto dto);
    void delete(Long id);



}
