package org.example.supplychainx.approvisionnement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyOrderStatusChangeDto {
    @NotNull
    private String status; // one of SupplyOrderStatus enum names
    private String comment;
}
