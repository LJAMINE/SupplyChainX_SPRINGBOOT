package org.example.supplychainx.approvisionnement.mapper;

import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.approvisionnement.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDto dto);
    SupplierResponseDto toDto(Supplier entity);
    void updateEntityFromDto(SupplierRequestDto dto, @MappingTarget Supplier entity);
}