package com.linh.lms.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class JwtServiceTest {
    private JwtService jwtService = new JwtService();

    @BeforeEach
    public void setSecrecForTesting() {
        ReflectionTestUtils.setField(jwtService, "secret", "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
    }

    @Test
    public void getUseridByToken() {
        String token = jwtService.generateToken(15L);
        String userId = jwtService.extractUserId(token);
        assertEquals("15", userId);
        assertFalse(token.isEmpty());
    }

    @Test
    public void getUseridByTokenInvalid() {
        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUserId("invalid");
        });
    }

    @Test
    public void getUseridByTokenExpired() {
        String token = Jwts.builder()
                .subject("15")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(jwtService.getSecretKey())
                .compact();
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUserId(token);
        });
    }

    @Test
    public void getUseridByTokenWithOtherSecret() {
        String token = Jwts.builder()
                .subject("15")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("AZEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")))
                .compact();

        assertThrows(SignatureException.class, () -> {
            jwtService.extractUserId(token);
        });
    }
}