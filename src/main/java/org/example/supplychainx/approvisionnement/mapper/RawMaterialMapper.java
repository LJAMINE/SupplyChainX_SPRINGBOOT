package org.example.supplychainx.approvisionnement.mapper;

import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.RawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RawMaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RawMaterial toEntity(RawMaterialRequestDto dto);

    RawMaterialResponseDto toDto(RawMaterial entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(RawMaterialRequestDto dto, @MappingTarget RawMaterial entity);
}