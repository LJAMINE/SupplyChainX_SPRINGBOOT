package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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


public class SupplierRequestDto {
    @NotBlank
    private String name;

    private String contact;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;

    @Min(0)
    private Integer leadTime;

}
