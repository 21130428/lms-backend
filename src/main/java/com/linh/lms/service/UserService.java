package com.linh.lms.service;

import com.linh.lms.dto.LoginRequest;
import com.linh.lms.dto.LoginResponse;
import com.linh.lms.dto.RegisterRequest;
import com.linh.lms.dto.RegisterResponse;
import com.linh.lms.entity.User;
import com.linh.lms.exception.*;
import com.linh.lms.repository.RefreshTokenRepository;
import com.linh.lms.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã được dùng");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username đã được dùng");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password không khớp với PasswordConfirm");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return new RegisterResponse(request.getUsername(), request.getEmail());
    }

    public LoginResponse login(LoginRequest request) throws NoSuchAlgorithmException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Mật khẩu hoặc email không chính xác"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Mật khẩu hoặc email không chính xác");
        }
        if (!user.isStatus()) {
            throw new AccountLockedException("Tài khoản của bạn đã bị khóa");
        }
        String token = jwtService.generateToken(user.getId());
        String refreshToken = refreshTokenService.getToken(user);
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), token, refreshToken);
    }
    
}