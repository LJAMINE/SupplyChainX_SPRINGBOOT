package org.example.supplychainx.livraison.mapper;

import org.example.supplychainx.livraison.dto.*;
import org.example.supplychainx.livraison.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    Customer toEntity(CustomerRequestDto dto);

    @Mapping(target = "id", source = "id")
    CustomerResponseDto toDto(Customer entity);
}