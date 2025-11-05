package org.example.supplychainx.livraison.mapper;

import org.example.supplychainx.livraison.dto.DeliveryRequestDto;
import org.example.supplychainx.livraison.dto.DeliveryResponseDto;
import org.example.supplychainx.livraison.entity.Delivery;
import org.example.supplychainx.livraison.entity.ClientOrder;
import org.example.supplychainx.livraison.entity.DeliveryStatus;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public Delivery toEntity(DeliveryRequestDto dto, ClientOrder clientOrder) {
        if (dto == null) return null;
        Delivery d = new Delivery();
        d.setClientOrder(clientOrder);
        d.setVehicle(dto.getVehicle());
        d.setDriver(dto.getDriver());
        d.setDeliveryDate(dto.getDeliveryDate());
        d.setCost(dto.getCost());
        // caller should set initial status
        return d;
    }

    public DeliveryResponseDto toDto(Delivery entity) {
        if (entity == null) return null;
        DeliveryResponseDto dto = new DeliveryResponseDto();
        dto.setId(entity.getId());
        dto.setClientOrderId(entity.getClientOrder() != null ? entity.getClientOrder().getId() : null);
        dto.setVehicle(entity.getVehicle());
        dto.setDriver(entity.getDriver());
        dto.setStatus(entity.getStatus() != null ? DeliveryStatus.valueOf(entity.getStatus().name()) : null);
        dto.setDeliveryDate(entity.getDeliveryDate());
        dto.setCost(entity.getCost());
        return dto;
    }
}