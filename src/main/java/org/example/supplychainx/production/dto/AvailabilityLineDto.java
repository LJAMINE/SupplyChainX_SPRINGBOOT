package org.example.supplychainx.production.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityLineDto {
    private Long rawMaterialId;
    private String rawMaterialName;
    private Integer requiredQty;
    private Integer availableQty;
    private Integer missingQty;
}