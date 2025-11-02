package org.example.supplychainx.production.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer productionTime;
    private Double cost;
    private Integer stock;
    private Integer stockMin;
    private List<BillOfMaterialDto> billOfMaterials;
    private LocalDate createdAt;
}