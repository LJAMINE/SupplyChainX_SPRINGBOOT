package org.example.supplychainx.production.controller;


import lombok.AllArgsConstructor;
//import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.production.dto.*;
import org.example.supplychainx.production.service.interf.ProductionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/production-orders")
public class ProductionOrderController {

    private final ProductionOrderService service;


//    @RequireRole({Role.CHEF_PRODUCTION})
    @PostMapping
    public ResponseEntity<ProductionOrderResponseDto> create(@Validated @RequestBody ProductionOrderRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

//    @RequireRole({Role.CHEF_PRODUCTION})
    @PutMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDto> update(@PathVariable Long id, @Validated @RequestBody ProductionOrderRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

//    @RequireRole({Role.CHEF_PRODUCTION, Role.SUPERVISEUR_PRODUCTION})
    @GetMapping
    public ResponseEntity<Page<ProductionOrderResponseDto>> list(
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(pageable));
    }

//    @RequireRole({Role.CHEF_PRODUCTION, Role.SUPERVISEUR_PRODUCTION})
    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

//    @RequireRole({Role.CHEF_PRODUCTION})
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductionOrderResponseDto> changeStatus(@PathVariable Long id, @Validated @RequestBody ProductionOrderStatusChangeDto dto) {
        return ResponseEntity.ok(service.changeStatus(id, dto));
    }

//    @RequireRole({Role.CHEF_PRODUCTION, Role.SUPERVISEUR_PRODUCTION})
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponseDto> availability(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.checkAvailability(productId, quantity));
    }

//    @RequireRole({Role.CHEF_PRODUCTION, Role.SUPERVISEUR_PRODUCTION})
    @GetMapping("/estimate")
    public ResponseEntity<EstimateResponseDto> estimate(
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.estimate(productId, quantity));
    }

}
