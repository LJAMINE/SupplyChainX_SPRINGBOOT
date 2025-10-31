package org.example.supplychainx.approvisionnement.service.interf;

import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderStatusChangeDto;
import org.example.supplychainx.approvisionnement.entity.SupplyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplyOrderService {

    Page<SupplyOrderResponseDto> list (String q, Pageable pageable);
    SupplyOrderResponseDto get (Long id);
    SupplyOrderResponseDto create (SupplyOrderRequestDto dto);
    SupplyOrderResponseDto update (Long id ,SupplyOrderRequestDto dto);
    void delete(Long id);
    SupplyOrderResponseDto changeStatus(Long id, SupplyOrderStatusChangeDto dto);


}
