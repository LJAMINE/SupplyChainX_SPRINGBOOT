package org.example.supplychainx.approvisionnement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SupplyOrderResponseDto {
    private Long id;

    private Long supplierId;
    private String supplierName;

    private LocalDate orderDate;
    private LocalDate expectedDate;

    private String status;

    private BigDecimal totalAmount;

    private List<SupplyOrderItemResponseDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}