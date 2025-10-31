package org.example.supplychainx.approvisionnement.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyOrderItemResponseDto {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;


}