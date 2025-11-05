package org.example.supplychainx.livraison.dto;

import lombok.Data;

import java.time.LocalDate;

@Data

public class DeliveryRequestDto {
    private Long clientOrderId;
    private String vehicle;
    private String driver;
    private LocalDate deliveryDate;
    private Double cost;
}