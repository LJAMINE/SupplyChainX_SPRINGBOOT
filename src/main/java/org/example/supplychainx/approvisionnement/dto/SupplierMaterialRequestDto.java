package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierMaterialRequestDto {

    @NotNull
    private Long supplierId;

    @NotNull
    private Long rawMaterialId;

    private String supplierSku;

    @PositiveOrZero
    private Double price;

    @Min(0)
    private Integer leadTime;

    @Min(0)
    private Integer minOrderQuantity;
}
