package org.example.supplychainx.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {
    @NotBlank
    private String name;

    // hours per unit
    @NotNull
    private Integer productionTime;

    private Double cost;
    private Integer stock;
    private Integer stockMin;

    // BOM entries to create/replace for the product
    private List<BillOfMaterialDto> billOfMaterials;
}