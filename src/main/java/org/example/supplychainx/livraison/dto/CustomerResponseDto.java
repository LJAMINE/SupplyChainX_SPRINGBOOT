package org.example.supplychainx.livraison.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private LocalDate createdAt;
    private LocalDateTime updatedAt;
}
