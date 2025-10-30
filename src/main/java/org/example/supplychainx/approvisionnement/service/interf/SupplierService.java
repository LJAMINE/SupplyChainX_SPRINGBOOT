package org.example.supplychainx.approvisionnement.service.interf;

import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

    Page<SupplierResponseDto> list(String q, Pageable pageable);
    SupplierResponseDto get (Long id);
     SupplierResponseDto create (SupplierRequestDto dto);
    SupplierResponseDto update (Long id ,SupplierRequestDto dto);
    void delete (Long id );


}
