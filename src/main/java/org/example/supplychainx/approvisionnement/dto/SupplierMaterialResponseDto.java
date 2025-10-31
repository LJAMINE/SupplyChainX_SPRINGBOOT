package org.example.supplychainx.approvisionnement.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierMaterialResponseDto {
    private Long id;

    private Long supplierId;
    private String supplierName;

    private Long rawMaterialId;
    private String rawMaterialName;

    private String supplierSku;
    private Double price;
    private Integer leadTime;
    private Integer minOrderQuantity;

    private LocalDateTime createdAt;
}