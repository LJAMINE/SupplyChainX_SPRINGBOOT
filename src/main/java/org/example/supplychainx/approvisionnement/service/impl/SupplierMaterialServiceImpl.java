package org.example.supplychainx.approvisionnement.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.SupplierMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.example.supplychainx.approvisionnement.entity.SupplierMaterial;
import org.example.supplychainx.approvisionnement.mapper.SupplierMaterialMapper;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.approvisionnement.repository.SupplierMaterialRepository;
import org.example.supplychainx.approvisionnement.repository.SupplierRepository;
import org.example.supplychainx.approvisionnement.service.interf.SupplierMaterialService;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
@Transactional
public class SupplierMaterialServiceImpl implements SupplierMaterialService {

    private final SupplierMaterialRepository repository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierMaterialMapper mapper;

    @Override
    public Page<SupplierMaterialResponseDto> list(String s, Pageable pageable) {
        Page<SupplierMaterial> page = repository.findAll(pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public Page<SupplierMaterialResponseDto> listBySupplierId(Long supplierId, Pageable pageable) {
        Page<SupplierMaterial> page = repository.findBySupplierId(supplierId, pageable);
        return page.map(mapper::toDto);
    }


    @Override
    public Page<SupplierMaterialResponseDto> listByRawMaterialId(Long rawmaterialId, Pageable pageable) {
        Page<SupplierMaterial> page = repository.findByRawMaterial_Id(rawmaterialId, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public SupplierMaterialResponseDto get(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(() -> new ResourceNotFoundException("not found "));
    }

    @Override
    public SupplierMaterialResponseDto create(SupplierMaterialRequestDto dto) {


//        validate that the supplier and material are in

        Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        RawMaterial rawMaterial = rawMaterialRepository.findById(dto.getRawMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found"));

//        no dulplicate

        if (repository.existsBySupplierIdAndRawMaterialId(supplier.getId(), rawMaterial.getId())) {
            throw new IllegalArgumentException("this supplierMaterial already exist for this material and row material ");
        }

//        placing the val
        SupplierMaterial entity = mapper.toEntity(dto);
        entity.setSupplier(supplier);
        entity.setRawMaterial(rawMaterial);
        entity.setCreatedAt(LocalDateTime.now());

        SupplierMaterial saved = repository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public SupplierMaterialResponseDto update(Long id, SupplierMaterialRequestDto dto) {
        SupplierMaterial entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierMaterial not found: " + id));

        // If supplier/rawMaterial changed, re-resolve and set them (or reject change)
        if (dto.getSupplierId() != null && !dto.getSupplierId().equals(entity.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + dto.getSupplierId()));
            entity.setSupplier(supplier);
        }
        if (dto.getRawMaterialId() != null && !dto.getRawMaterialId().equals(entity.getRawMaterial().getId())) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(dto.getRawMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + dto.getRawMaterialId()));
            entity.setRawMaterial(rawMaterial);
        }

        mapper.updateEntityFromDto(dto, entity);
        SupplierMaterial saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("not found supplier_material_ID");
        }

        repository.deleteById(id);
    }
}
