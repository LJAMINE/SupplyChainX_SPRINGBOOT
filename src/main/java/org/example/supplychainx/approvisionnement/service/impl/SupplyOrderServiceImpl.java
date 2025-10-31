package org.example.supplychainx.approvisionnement.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderItemRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplyOrderStatusChangeDto;
import org.example.supplychainx.approvisionnement.entity.*;
import org.example.supplychainx.approvisionnement.mapper.SupplyOrderMapper;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.example.supplychainx.approvisionnement.repository.SupplyOrderItemRepository;
import org.example.supplychainx.approvisionnement.repository.SupplyOrderRepository;
import org.example.supplychainx.approvisionnement.service.interf.SupplyOrderService;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class SupplyOrderServiceImpl implements SupplyOrderService {

    private final SupplyOrderRepository repository;
    private  final SupplyOrderItemRepository supplyOrderItemRepository;
    private  final RawMaterialRepository rawMaterialRepository;
    private  final SupplierRepository supplierRepository;
    private final SupplyOrderMapper mapper;


    @Override
    public Page<SupplyOrderResponseDto> list(String q, Pageable pageable) {
        Page<SupplyOrder> page = repository.findAll(pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public SupplyOrderResponseDto get(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(() -> new ResourceNotFoundException("not found "));
    }

    @Override
    public SupplyOrderResponseDto create(SupplyOrderRequestDto dto) {

//        insert in the supplyOrder table first
        Supplier supplier =supplierRepository.findById(dto.getSupplierId()).orElseThrow(()->new ResourceNotFoundException("supplier not found "));

        SupplyOrder supplyOrder= mapper.toEntity(dto);
        supplyOrder.setSupplier(supplier);
        supplyOrder.setOrderDate(LocalDate.now());
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);

//

        List<SupplyOrderItem> items=new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getItems() != null) {
            for (SupplyOrderItemRequestDto itDto : dto.getItems()) {
                RawMaterial rm = rawMaterialRepository.findById(itDto.getRawMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + itDto.getRawMaterialId()));

                BigDecimal unitPrice = itDto.getUnitPrice() != null ? itDto.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itDto.getQuantity()));

                SupplyOrderItem item = SupplyOrderItem.builder()
                        .rawMaterial(rm)
                        .quantity(itDto.getQuantity())
                        .unitPrice(unitPrice)
                        .lineTotal(lineTotal)
                        .supplyOrder(supplyOrder)
                        .build();

                items.add(item);
                total = total.add(lineTotal);
            }
        }
        supplyOrder.setItems(items);
        supplyOrder.setTotalAmount(total);

        SupplyOrder saved = repository.save(supplyOrder);
        return mapper.toDto(saved);
    }

    @Override
    public SupplyOrderResponseDto update(Long id, SupplyOrderRequestDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("not found ");
        }
        repository.deleteById(id);
    }

    @Override
    public SupplyOrderResponseDto changeStatus(Long id, SupplyOrderStatusChangeDto dto) {
        return null;
    }
}
