package org.example.supplychainx.livraison.controller;

import lombok.AllArgsConstructor;
import org.example.supplychainx.livraison.dto.ClientOrderRequestDto;
import org.example.supplychainx.livraison.dto.ClientOrderResponseDto;
import org.example.supplychainx.livraison.dto.ClientOrderWithAvailabilityDto;
import org.example.supplychainx.livraison.service.interf.ClientOrderService;
import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.livraison.dto.AvailabilityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/client-orders")
public class ClientOrderController {

    private final ClientOrderService service;

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PostMapping
    public ResponseEntity<ClientOrderWithAvailabilityDto> create(@Validated @RequestBody ClientOrderRequestDto dto) {
        ClientOrderResponseDto created = service.create(dto);
        AvailabilityResponseDto availability = service.checkAvailability(created.getId());

        ClientOrderWithAvailabilityDto resp = ClientOrderWithAvailabilityDto.builder()
                .order(created)
                .availability(availability)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


}