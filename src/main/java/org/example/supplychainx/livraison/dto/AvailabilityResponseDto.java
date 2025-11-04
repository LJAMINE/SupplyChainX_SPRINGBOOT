package org.example.supplychainx.livraison.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponseDto {
    private boolean available;
    private List<AvailabilityLineDto> missing;
}