package org.example.supplychainx.production.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOfMaterialDto {
    private Long id;
    @NotNull
    private Long rawMaterialId;
    @NotNull
    @Min(1)
    private Integer quantityRequired;
}