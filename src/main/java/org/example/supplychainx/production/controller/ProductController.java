package org.example.supplychainx.production.controller;


import lombok.AllArgsConstructor;
//import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.production.dto.ProductRequestDto;
import org.example.supplychainx.production.dto.ProductResponseDto;
import org.example.supplychainx.production.service.interf.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

//    @RequireRole({Role.SUPERVISEUR_PRODUCTION})
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(pageable));
    }

//    @RequireRole({Role.SUPERVISEUR_PRODUCTION})
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchByName(
            @RequestParam("q") String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.searchByName(q, pageable));
    }

//    @RequireRole({Role.CHEF_PRODUCTION})
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Validated @RequestBody ProductRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

//    @RequireRole({Role.CHEF_PRODUCTION})
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long id, @Validated @RequestBody ProductRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

//    @RequireRole({Role.CHEF_PRODUCTION})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @RequireRole({Role.SUPERVISEUR_PRODUCTION, Role.CHEF_PRODUCTION})
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
}
