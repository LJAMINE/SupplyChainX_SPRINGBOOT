package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RawMaterialRequestDto {
    @NotBlank
    private String name;

    @Min(0)
    private Integer stock;

    @Min(0)
    private Integer stockMin;

    private String unit;
}
