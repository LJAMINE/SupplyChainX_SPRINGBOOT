package org.example.supplychainx.livraison.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientOrderRequestDto {

    @NotNull
    private Long customerId;

    @Size(min = 1)
    private List<ClientOrderItemDto> items;
}
