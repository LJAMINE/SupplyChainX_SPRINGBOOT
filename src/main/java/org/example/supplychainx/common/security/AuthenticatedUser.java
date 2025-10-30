package org.example.supplychainx.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AuthenticatedUser {
    private Long id;
    private String email;
    private Role role;
}