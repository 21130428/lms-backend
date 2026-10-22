package com.linh.lms.controller;

import com.linh.lms.dto.*;
import com.linh.lms.service.RefreshTokenService;
import com.linh.lms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) throws NoSuchAlgorithmException {
        return userService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest request) throws NoSuchAlgorithmException {
        return refreshTokenService.refreshToken(request);
    }
}
