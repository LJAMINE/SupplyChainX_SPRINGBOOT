package org.example.supplychainx.livraison.controller;


import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.SupplierRequestDto;
import org.example.supplychainx.approvisionnement.dto.SupplierResponseDto;
import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.livraison.dto.CustomerRequestDto;
import org.example.supplychainx.livraison.dto.CustomerResponseDto;
import org.example.supplychainx.livraison.entity.Customer;
import org.example.supplychainx.livraison.service.interf.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")

public class CustomerController {
    private  final CustomerService service;

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(s, pageable));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }


    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Validated @RequestBody CustomerRequestDto dto) {
//        System.out.println("DEBUG: SupplierController.create received DTO = " + dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long id, @Validated @RequestBody CustomerRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL, Role.SUPERVISEUR_LOGISTIQUE})
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerResponseDto>> searchByName(
            @RequestParam("q") String q,
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.searchByName(q, pageable));
    }

}
