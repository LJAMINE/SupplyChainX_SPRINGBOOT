package org.example.supplychainx.approvisionnement.service.interf;

import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RawMaterialService {
    Page<RawMaterialResponseDto> list(String q, Pageable pageable);
    RawMaterialResponseDto get(Long id);
    RawMaterialResponseDto create(RawMaterialRequestDto dto);
    RawMaterialResponseDto update(Long id, RawMaterialRequestDto dto);
    void delete(Long id);
}