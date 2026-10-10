package com.linh.lms.service;

import com.linh.lms.dto.RegisterRequest;
import com.linh.lms.dto.RegisterResponse;
import com.linh.lms.entity.User;
import com.linh.lms.exception.EmailAlreadyExistsException;
import com.linh.lms.exception.UsernameAlreadyExistsException;
import com.linh.lms.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setUsername(request.getUsername());
        response.setEmail(request.getEmail());
        return response;
    }

}