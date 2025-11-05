package org.example.supplychainx.livraison.dto;

import lombok.Data;
import org.example.supplychainx.livraison.entity.DeliveryStatus;

import java.time.LocalDate;

@Data
public class DeliveryResponseDto {
    private Long id;
    private Long clientOrderId;
    private String vehicle;
    private String driver;
    private DeliveryStatus status;
    private LocalDate deliveryDate;
    private Double cost;
}