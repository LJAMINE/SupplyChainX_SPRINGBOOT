package org.example.supplychainx.common.security;

import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.administration.repository.UserRepository;
import org.example.supplychainx.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validate credentials (dev simple check). In production, use hashed passwords.
     */
    public AuthenticatedUser authenticate(String email, String password) {
        if (email == null || password == null) {
            throw new UnauthorizedException("Missing credentials");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        // DEV: plaintext check — replace with BCrypt in production
        if (!password.equals(user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole());
    }
}