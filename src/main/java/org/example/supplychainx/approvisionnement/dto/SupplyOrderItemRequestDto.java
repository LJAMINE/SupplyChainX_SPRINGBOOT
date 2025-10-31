package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyOrderItemRequestDto {
    @NotNull
    private Long rawMaterialId;

    @NotNull
    @Min(1)
    private Integer quantity;

     private BigDecimal unitPrice;
}