package org.example.supplychainx.approvisionnement.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponseDto {
    private Long id;
    private String name;
    private String contact;
    private Double rating;
    private Integer leadTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}