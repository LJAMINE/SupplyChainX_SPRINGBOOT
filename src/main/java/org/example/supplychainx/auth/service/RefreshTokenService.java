package org.example.supplychainx.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.auth.entity.RefreshToken;
import org.example.supplychainx.auth.repository.RefreshTokenRepository;
import org.example.supplychainx.security.JwtTokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Create and persist a refresh token for a user
     */
    public String createRefreshToken(User user) {
        // Generate the JWT refresh token
        String token = jwtTokenUtil.generateRefreshToken(user.getEmail());
        
        // Hash the token before storing (security best practice)
        String tokenHash = hashToken(token);
        
        // Create and save the refresh token entity
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshToken);
        
        return token; // Return the actual JWT token (not the hash)
    }

    /**
     * Validate refresh token: check if exists in DB, not expired, not revoked
     */
    public RefreshToken validateRefreshToken(String token) {
        // First validate JWT structure and signature
        if (!jwtTokenUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        String tokenHash = hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        
        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }
        
        if (refreshToken.isExpired()) {
            throw new IllegalArgumentException("Refresh token has expired");
        }
        
        return refreshToken;
    }

    /**
     * Rotate refresh token: revoke old one and create new one
     */
    public String rotateRefreshToken(String oldToken, User user) {
        // Validate and revoke the old token
        RefreshToken oldRefreshToken = validateRefreshToken(oldToken);
        revokeToken(oldRefreshToken);
        
        // Create and return new token
        return createRefreshToken(user);
    }

    /**
     * Revoke a specific refresh token
     */
    public void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices)
     */
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Clean up expired tokens (should be run periodically)
     */
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    /**
     * Hash token using SHA-256 before storing in database
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
