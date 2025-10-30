package org.example.supplychainx.administration.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.supplychainx.common.security.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;

    @NotNull
    private Role role;
}