package org.example.supplychainx.administration.controller;

import org.example.supplychainx.administration.dto.UserRequestDto;
import org.example.supplychainx.administration.dto.UserResponseDto;
import org.example.supplychainx.administration.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // List users with optional search (firstName, lastName, email)
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(service.list(q, pageable));
    }

    // Get single user
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    // Create user (keep unprotected initially so you can bootstrap the first admin)
//    @PostMapping
//    public ResponseEntity<UserResponseDto> create(@Validated @RequestBody UserRequestDto dto) {
//        return ResponseEntity.ok(service.create(dto));
//    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Validated @RequestBody UserRequestDto dto) {
        System.out.println("DEBUG Controller: received DTO = " + dto);
        return ResponseEntity.ok(service.create(dto));
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @Validated @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}