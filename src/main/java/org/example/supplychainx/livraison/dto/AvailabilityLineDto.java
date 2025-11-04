package org.example.supplychainx.livraison.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityLineDto {
    private Long productId;
    private String productName;
    private Integer requiredQty;
    private Integer availableQty;
    private Integer missingQty;
}