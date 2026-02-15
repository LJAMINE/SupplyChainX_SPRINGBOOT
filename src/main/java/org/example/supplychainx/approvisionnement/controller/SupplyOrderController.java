package org.example.supplychainx.approvisionnement.controller;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.*;
import org.example.supplychainx.approvisionnement.service.interf.SupplyOrderService;
//import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/supply-orders")
public class SupplyOrderController {

    private final SupplyOrderService service;

    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','GESTIONNAIRE_APPROVISIONNEMENT','SUPERVISEUR_LOGISTIQUE')")
    @GetMapping
    public ResponseEntity<Page<SupplyOrderResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(s, pageable));
    }



    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','SUPERVISEUR_LOGISTIQUE')")
    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    @PostMapping
    public ResponseEntity<SupplyOrderResponseDto> create(@Validated @RequestBody SupplyOrderRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDto> update(@PathVariable Long id, @Validated @RequestBody SupplyOrderRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPERVISEUR_LOGISTIQUE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplyOrderResponseDto> changeStatus(@PathVariable Long id, @Validated @RequestBody SupplyOrderStatusChangeDto dto) {
        return ResponseEntity.ok(service.changeStatus(id, dto));
    }
}