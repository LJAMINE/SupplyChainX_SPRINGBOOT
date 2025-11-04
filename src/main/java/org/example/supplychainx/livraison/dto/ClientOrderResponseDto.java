package org.example.supplychainx.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.supplychainx.livraison.entity.ClientOrderStatus;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClientOrderResponseDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDate orderDate;
    private String status;
    private List<ClientOrderItemDto> items;
    private Double totalAmount;


}
