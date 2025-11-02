package org.example.supplychainx.production.service.interf;

import org.example.supplychainx.production.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionOrderService {
    ProductionOrderResponseDto create(ProductionOrderRequestDto dto);
    ProductionOrderResponseDto update(Long id, ProductionOrderRequestDto dto);
    void delete(Long id);
    ProductionOrderResponseDto get(Long id);
    Page<ProductionOrderResponseDto> list(Pageable pageable);

    ProductionOrderResponseDto changeStatus(Long id, ProductionOrderStatusChangeDto dto);

    AvailabilityResponseDto checkAvailability(Long productId, Integer quantity);
    EstimateResponseDto estimate(Integer productId, Integer quantity);
}