package com.linh.lms.controller;

import com.linh.lms.dto.LoginRequest;
import com.linh.lms.dto.LoginResponse;
import com.linh.lms.dto.RegisterRequest;
import com.linh.lms.dto.RegisterResponse;
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

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) throws NoSuchAlgorithmException {
        return userService.login(request);
    }
}
