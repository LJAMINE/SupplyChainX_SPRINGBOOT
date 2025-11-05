package org.example.supplychainx.livraison.service.interf;

import org.example.supplychainx.livraison.dto.ClientOrderResponseDto;
import org.example.supplychainx.livraison.dto.DeliveryRequestDto;
import org.example.supplychainx.livraison.dto.DeliveryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryService {
    Page<DeliveryResponseDto> list(Pageable pageable);

    DeliveryResponseDto get(Long id);

    DeliveryResponseDto create(DeliveryRequestDto dto);

    DeliveryResponseDto update(Long id, DeliveryRequestDto dto);

    void delete(Long id);

    DeliveryResponseDto changeStatus(Long id, String status);



}
