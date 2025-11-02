package org.example.supplychainx.production.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionOrderStatusChangeDto {
    @NotNull
    private String status; // EN_PRODUCTION, TERMINE, BLOQUE, ANNULLE
}