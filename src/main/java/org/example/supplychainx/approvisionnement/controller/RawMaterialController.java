package org.example.supplychainx.approvisionnement.controller;

import lombok.AllArgsConstructor;
import org.example.supplychainx.approvisionnement.dto.RawMaterialRequestDto;
import org.example.supplychainx.approvisionnement.dto.RawMaterialResponseDto;
import org.example.supplychainx.approvisionnement.service.interf.RawMaterialService;
//import org.example.supplychainx.common.security.RequireRole;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.config.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/raw-materials")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    //    @RequireRole({Role.RESPONSABLE_ACHATS, Role.GESTIONNAIRE_APPROVISIONNEMENT})
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','GESTIONNAIRE_APPROVISIONNEMENT')")

    @GetMapping
    public ResponseEntity<Page<RawMaterialResponseDto>> list(
            @RequestParam(value = "s", required = false) String s,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(rawMaterialService.list(s, pageable));
    }




    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "id", principal.getId(),
                        "email", principal.getEmail(),
                        "role", principal.getRole()
                )
        );
    }

//    @RequireRole({Role.RESPONSABLE_ACHATS, Role.SUPERVISEUR_LOGISTIQUE})

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<RawMaterialResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(rawMaterialService.get(id));
    }

    //    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})
    @PostMapping
//    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<RawMaterialResponseDto> create(@Validated @RequestBody RawMaterialRequestDto dto) {
        return ResponseEntity.ok(rawMaterialService.create(dto));
    }

//    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<RawMaterialResponseDto> update(@PathVariable Long id, @Validated @RequestBody RawMaterialRequestDto dto) {
        return ResponseEntity.ok(rawMaterialService.update(id, dto));
    }

//    @RequireRole({Role.GESTIONNAIRE_APPROVISIONNEMENT})

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        rawMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}