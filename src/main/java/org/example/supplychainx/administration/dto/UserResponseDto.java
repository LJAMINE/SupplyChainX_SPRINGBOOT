package org.example.supplychainx.administration.dto;

import lombok.*;
import org.example.supplychainx.common.security.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}