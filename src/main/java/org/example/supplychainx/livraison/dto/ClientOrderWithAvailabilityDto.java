package org.example.supplychainx.livraison.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOrderWithAvailabilityDto {
    private ClientOrderResponseDto order;
    private AvailabilityResponseDto availability;
}