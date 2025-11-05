package org.example.supplychainx.livraison.controller;

import lombok.AllArgsConstructor;
import org.example.supplychainx.livraison.dto.*;
import org.example.supplychainx.livraison.service.interf.ClientOrderService;
import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.production.dto.ProductionOrderStatusChangeDto;
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

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL, Role.SUPERVISEUR_LOGISTIQUE})
    @GetMapping
    public ResponseEntity<Page<ClientOrderResponseDto>> list(
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(pageable));
    }


    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @GetMapping("/{id}")
    public ResponseEntity<ClientOrderResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClientOrderResponseDto> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(service.changeStatus(id, status));
    }



    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDto> availability(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.checkAvailability(id));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PostMapping("/{id}/create-production-orders")
    public ResponseEntity<?> createProduction(@PathVariable Long id) {
        service.createProductionForShortages(id);
        return ResponseEntity.ok().build();
    }

//


}