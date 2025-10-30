package org.example.supplychainx.approvisionnement.controller;


import lombok.AllArgsConstructor;
import org.example.supplychainx.administration.dto.UserRequestDto;
import org.example.supplychainx.administration.dto.UserResponseDto;
import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.approvisionnement.service.interf.SupplierService;
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
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;


    @RequireRole({Role.RESPONSABLE_ACHATS})

    @GetMapping
    public ResponseEntity<Page<SupplierResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(supplierService.list(s, pageable));
    }


    @RequireRole({Role.SUPERVISEUR_LOGISTIQUE})
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.get(id));
    }



    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})
    @PostMapping
    public ResponseEntity<SupplierResponseDto> create(@Validated @RequestBody SupplierRequestDto dto) {
        return ResponseEntity.ok(supplierService.create(dto));
    }


    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> update(@PathVariable Long id, @Validated @RequestBody SupplierRequestDto dto) {
        return ResponseEntity.ok(supplierService.update(id, dto));
    }


    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        supplierService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
