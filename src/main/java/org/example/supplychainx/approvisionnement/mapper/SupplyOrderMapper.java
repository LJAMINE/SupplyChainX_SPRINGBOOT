package org.example.supplychainx.approvisionnement.mapper;

import org.example.supplychainx.approvisionnement.dto.*;
import org.example.supplychainx.approvisionnement.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyOrderMapper {

    // request -> entity (skip relational wiring; service sets supplier and rawMaterial relations)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    SupplyOrder toEntity(SupplyOrderRequestDto dto);

//     map item entity -> response dto (extract raw material id/name)
    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "rawMaterialName", source = "rawMaterial.name")
    SupplyOrderItemResponseDto toItemDto(SupplyOrderItem item);

    // map order entity -> response dto (extract supplier id/name)
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    SupplyOrderResponseDto toDto(SupplyOrder order);
}