package org.example.supplychainx.approvisionnement.controller;


import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplierMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierMaterialResponseDto;
import org.example.supplychainx.approvisionnement.entity.SupplierMaterial;
import org.example.supplychainx.approvisionnement.service.interf.SupplierMaterialService;
import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/supplier-materials")
public class SupplierMaterialController {

    private final SupplierMaterialService supplierMaterialService;

    @RequireRole({Role.SUPERVISEUR_LOGISTIQUE})
    @GetMapping
    public ResponseEntity<Page<SupplierMaterialResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(supplierMaterialService.list(s, pageable));
    }

    @RequireRole({ Role.SUPERVISEUR_LOGISTIQUE})

    @GetMapping("/{id}")
    public ResponseEntity<SupplierMaterialResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(supplierMaterialService.get(id));
    }



    @RequireRole({Role.RESPONSABLE_ACHATS})
    @PostMapping
    public ResponseEntity<SupplierMaterialResponseDto> create(@Validated @RequestBody SupplierMaterialRequestDto dto) {
        return ResponseEntity.ok(supplierMaterialService.create(dto));
    }

    @RequireRole({Role.RESPONSABLE_ACHATS})
    @PutMapping("/{id}")
    public ResponseEntity<SupplierMaterialResponseDto> update(@PathVariable Long id, @Validated @RequestBody SupplierMaterialRequestDto dto) {
        return ResponseEntity.ok(supplierMaterialService.update(id, dto));
    }


    @RequireRole({Role.RESPONSABLE_ACHATS})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        supplierMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
