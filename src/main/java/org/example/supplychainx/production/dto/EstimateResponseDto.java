package org.example.supplychainx.production.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateResponseDto {
    private Integer estimatedHours;
}