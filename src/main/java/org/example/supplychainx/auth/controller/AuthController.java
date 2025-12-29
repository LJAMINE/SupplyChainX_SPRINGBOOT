package org.example.supplychainx.auth.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.administration.repository.UserRepository;
import org.example.supplychainx.auth.dto.LoginRequestDto;
import org.example.supplychainx.auth.service.RefreshTokenService;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.example.supplychainx.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is enabled
        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "User account is disabled"
            ));
        }

        // Generate access token
        String accessToken = jwtTokenUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        
        // Create and persist refresh token in database
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        try {
            // Validate refresh token from database
            var storedToken = refreshTokenService.validateRefreshToken(refreshToken);
            User user = storedToken.getUser();

            // Check if user is still enabled
            if (!user.isEnabled()) {
                return ResponseEntity.status(403).body(Map.of(
                        "message", "User account is disabled"
                ));
            }

            // Generate new access token
            String accessToken = jwtTokenUtil.generateAccessToken(user.getEmail(), user.getRole().name());
            
            // Rotate refresh token: revoke old and create new
            String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, user);

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", newRefreshToken
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        try {
            // Validate and revoke the refresh token
            var storedToken = refreshTokenService.validateRefreshToken(refreshToken);
            refreshTokenService.revokeToken(storedToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Logged out successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Invalid refresh token"
            ));
        }
    }


  


}