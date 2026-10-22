package com.linh.lms.service;

import com.linh.lms.dto.RefreshRequest;
import com.linh.lms.dto.RefreshResponse;
import com.linh.lms.entity.RefreshToken;
import com.linh.lms.entity.User;
import com.linh.lms.exception.InvalidRefreshTokenException;
import com.linh.lms.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String hashToken(String token) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    public String getToken(User user) throws NoSuchAlgorithmException {
        String token = generateToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hashToken(token));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

//    public boolean verifyToken(String token) throws NoSuchAlgorithmException {
//        Optional<RefreshToken> optional = refreshTokenRepository.findByTokenHash(hashToken(token));
//        if (optional.isEmpty()) {
//            return false;
//        }
//        RefreshToken refreshToken = optional.get();
//        return !refreshToken.isRevoked() && refreshToken.getExpiresAt().isAfter(Instant.now()) && refreshToken.getUser().isStatus();
//    }

    public Optional<RefreshToken> findValidToken(String token) throws NoSuchAlgorithmException {
        Optional<RefreshToken> optional = refreshTokenRepository.findByTokenHash(hashToken(token));
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        RefreshToken refreshToken = optional.get();
        if (!refreshToken.isRevoked() && refreshToken.getExpiresAt().isAfter(Instant.now()) && refreshToken.getUser().isStatus()) {
            return optional;
        }
        return Optional.empty();
    }

    public RefreshResponse refreshToken(RefreshRequest request) throws NoSuchAlgorithmException {
        Optional<RefreshToken> optional = findValidToken(request.getRefreshToken());
        if (optional.isEmpty()) {
            throw new InvalidRefreshTokenException("Refresh token không hợp lệ");
        }
        Long userId = optional.get().getUser().getId();
        String token = jwtService.generateToken(userId);
        return new RefreshResponse(token);
    }
}
