package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyOrderRequestDto {
    @NotNull
    private Long supplierId;


    private LocalDate expectedDate;

    private List<SupplyOrderItemRequestDto> items;
}