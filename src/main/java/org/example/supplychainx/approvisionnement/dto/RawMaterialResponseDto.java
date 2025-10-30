package org.example.supplychainx.approvisionnement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialResponseDto {
    private Long id;
    private String name;
    private Integer stockMin;
    private Integer stock;
    private LocalDateTime createdAt;
    private String unit;

}
