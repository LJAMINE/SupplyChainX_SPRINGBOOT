package org.example.supplychainx.livraison.mapper;

import org.example.supplychainx.livraison.dto.*;
import org.example.supplychainx.livraison.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientOrderMapper {

    default ClientOrder toEntity(ClientOrderRequestDto dto) {
        if (dto == null) return null;
        ClientOrder order = ClientOrder.builder().orderDate(java.time.LocalDate.now()).status(ClientOrderStatus.PLANIFIE).build();
        if (dto.getItems() != null) {
            order.setItems(dto.getItems().stream().map(i ->
                    ClientOrderItem.builder()
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .unitPrice(i.getUnitPrice())
                            .quantityDelivered(0)
                            .clientOrder(order)
                            .build()
            ).collect(Collectors.toList()));
        }
        return order;
    }

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "status", source = "status")
    ClientOrderResponseDto toDto(ClientOrder entity);

    default ClientOrderItemDto toItemDto(ClientOrderItem item) {
        if (item == null) return null;
        return ClientOrderItemDto.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}