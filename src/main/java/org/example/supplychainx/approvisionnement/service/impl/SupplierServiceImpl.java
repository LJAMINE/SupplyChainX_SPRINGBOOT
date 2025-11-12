package org.example.supplychainx.approvisionnement.service.impl;


import lombok.AllArgsConstructor;
import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.administration.mapper.UserMapper;
import org.example.supplychainx.administration.repository.UserRepository;
import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.approvisionnement.mapper.SupplierMapper;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.example.supplychainx.approvisionnement.repository.SupplyOrderRepository;
import org.example.supplychainx.approvisionnement.service.interf.SupplierService;
import org.example.supplychainx.common.exception.HasCommandeActive;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional

public class SupplierServiceImpl implements SupplierService {


    private final SupplierRepository supplierRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierMapper mapper;

    @Override
    public Page<SupplierResponseDto> list(String search, Pageable pageable) {
        Page<Supplier> page = (search == null || search.isBlank())
                ? supplierRepository.findAll(pageable)
                : supplierRepository.findByNameContainingIgnoreCase(search, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public SupplierResponseDto get(Long id) {
        return supplierRepository.findById(id).map(mapper::toDto).orElseThrow(() -> new ResourceNotFoundException("not found "));
    }



    @Override
    public SupplierResponseDto create(SupplierRequestDto dto) {
        Supplier supplier =mapper.toEntity(dto);
        supplier.setCreatedAt(LocalDateTime.now());
        Supplier saved =supplierRepository.save(supplier);
        return mapper.toDto(saved);
    }



    @Override
    public SupplierResponseDto update(Long id, SupplierRequestDto dto) {

        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));

        mapper.updateEntityFromDto(dto, supplier);
        supplier.setUpdatedAt(LocalDateTime.now());
        Supplier saved = supplierRepository.save(supplier);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {

        if (!supplierRepository.existsById(id)){
            throw new ResourceNotFoundException("supplier not found ");
        }

        if (supplyOrderRepository.existsBySupplierId(id)){
            throw new HasCommandeActive("has command active ");
        }
        supplierRepository.deleteById(id);

    }
}
