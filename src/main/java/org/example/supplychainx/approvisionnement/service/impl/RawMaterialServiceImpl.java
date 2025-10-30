package org.example.supplychainx.approvisionnement.service.impl;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.example.supplychainx.approvisionnement.mapper.RawMaterialMapper;
import org.example.supplychainx.approvisionnement.repository.RawMaterialRepository;
import org.example.supplychainx.approvisionnement.service.interf.RawMaterialService;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository repository;
    private final RawMaterialMapper mapper;

    @Override
    public Page<RawMaterialResponseDto> list(String q, Pageable pageable) {
        Page<RawMaterial> page = (q == null || q.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(q, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public RawMaterialResponseDto get(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + id));
    }

    @Override
    public RawMaterialResponseDto create(RawMaterialRequestDto dto) {

         if (dto.getName() != null && repository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Raw material name already exists");
        }

        RawMaterial entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        RawMaterial saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public RawMaterialResponseDto update(Long id, RawMaterialRequestDto dto) {
        RawMaterial entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found: " + id));
        mapper.updateEntityFromDto(dto, entity);
        RawMaterial saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Raw material not found: " + id);
        }
        repository.deleteById(id);
    }
}