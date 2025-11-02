package org.example.supplychainx.production.mapper;

import org.example.supplychainx.production.dto.*;
import org.example.supplychainx.production.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    // map request to entity (ignore relational wiring; service will set BOM relations)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Product toEntity(ProductRequestDto dto);

    // BOM dto -> entity (service will set product and resolve RawMaterial)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    BillOfMaterial toBomEntity(BillOfMaterialDto dto);

    // entity -> dto
    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "quantityRequired", source = "quantity")
    BillOfMaterialDto toBomDto(BillOfMaterial bom);

    @Mapping(target = "billOfMaterials", source = "billOfMaterials")
    ProductResponseDto toDto(Product product);
}