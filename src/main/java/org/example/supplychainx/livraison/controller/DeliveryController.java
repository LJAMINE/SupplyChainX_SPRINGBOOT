package org.example.supplychainx.livraison.controller;


import lombok.AllArgsConstructor;
//import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.livraison.dto.*;
import org.example.supplychainx.livraison.service.interf.DeliveryService;
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
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService service;

//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL, Role.SUPERVISEUR_LOGISTIQUE})
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDto>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(pageable));
    }

//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PostMapping
    public ResponseEntity<DeliveryResponseDto> create(@Validated @RequestBody DeliveryRequestDto dto) {
        DeliveryResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> update(@PathVariable Long id, @Validated @RequestBody DeliveryRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }


//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


//    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDto> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(service.changeStatus(id, status));
    }
}
