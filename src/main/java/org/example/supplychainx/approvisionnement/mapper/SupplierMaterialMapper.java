package org.example.supplychainx.approvisionnement.mapper;

import org.example.supplychainx.approvisionnement.dto.SupplierMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.SupplierMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SupplierMaterial toEntity(SupplierMaterialRequestDto dto);

    // Map entity -> response DTO and extract related ids/names
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "rawMaterialName", source = "rawMaterial.name")
    SupplierMaterialResponseDto toDto(SupplierMaterial entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(SupplierMaterialRequestDto dto, @MappingTarget SupplierMaterial entity);
}