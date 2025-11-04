package org.example.supplychainx.livraison.service.interf;

import org.example.supplychainx.livraison.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientOrderService {
    Page<ClientOrderResponseDto> list(String q, Pageable pageable);
    ClientOrderResponseDto get(Long id);
    ClientOrderResponseDto create(ClientOrderRequestDto dto);
    ClientOrderResponseDto update(Long id, ClientOrderRequestDto dto);
    void delete(Long id);
    ClientOrderResponseDto changeStatus(Long id, String status);
    AvailabilityResponseDto checkAvailability(Long id);
    void createProductionForShortages(Long id);
}