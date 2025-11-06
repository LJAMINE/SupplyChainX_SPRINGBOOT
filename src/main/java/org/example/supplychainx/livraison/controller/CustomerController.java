package org.example.supplychainx.livraison.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.livraison.dto.CustomerRequestDto;
import org.example.supplychainx.livraison.dto.CustomerResponseDto;
import org.example.supplychainx.livraison.service.interf.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Customer management endpoints.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Operations to manage cus    tomers")
@SecurityRequirement(name = "bearerAuth") // optional: shows Authorize button in Swagger UI
public class CustomerController {
    private final CustomerService service;

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @Operation(summary = "List customers", description = "List customers. Optional search and pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged list of customers"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(s, pageable));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @Operation(summary = "Get customer", description = "Get customer details by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @Operation(summary = "Create customer", description = "Create a new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Bad request / validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Validated @RequestBody CustomerRequestDto dto) {
        CustomerResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @Operation(summary = "Update customer", description = "Update an existing customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Bad request / validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long id, @Validated @RequestBody CustomerRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL})
    @Operation(summary = "Delete customer", description = "Delete a customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequireRole({Role.GESTIONNAIRE_COMMERCIAL, Role.SUPERVISEUR_LOGISTIQUE})
    @Operation(summary = "Search customers by name", description = "Search customers by name with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged search results"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerResponseDto>> searchByName(
            @RequestParam("q") String q,
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.searchByName(q, pageable));
    }
}