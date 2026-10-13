package com.linh.lms.service;

import com.linh.lms.dto.LoginRequest;
import com.linh.lms.dto.LoginResponse;
import com.linh.lms.dto.RegisterRequest;
import com.linh.lms.dto.RegisterResponse;
import com.linh.lms.entity.Role;
import com.linh.lms.entity.User;
import com.linh.lms.exception.*;
import com.linh.lms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User createActiveUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("student01");
        user.setEmail("student@gmail.com");
        user.setPassword("hashedPassword");
        user.setStatus(true);
        user.setRole(Role.STUDENT);
        return user;
    }

    @Test
    void loginSuccess() {
        // Arrange: chuẩn bị dữ liệu
        User user = createActiveUser();
        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPassword")).thenReturn(true);

        // Act: thực hiện đăng nhập
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("Password123");

        LoginResponse response = userService.login(request);

        // Assert: kiểm tra kết quả
        assertNotNull(response);
        assertEquals("student01", response.getUsername());
        assertEquals("student@gmail.com", response.getEmail());
        assertEquals(Role.STUDENT, response.getRole());
    }

    @Test
    void loginWrongPassword() {
        // Arrange
        User user = createActiveUser();
        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        // Act
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("WrongPassword");


        // Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(request);
        });
        assertEquals("Mật khẩu hoặc email không chính xác", exception.getMessage());
    }

    @Test
    void loginWrongEmail() {
        // Arrange
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        // Act
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@gmail.com");

        // Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(request);
        });
        assertEquals("Mật khẩu hoặc email không chính xác", exception.getMessage());
    }

    @Test
    void loginLockedUser() {
        // Arrange
        User user = createActiveUser();
        user.setStatus(false);
        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPassword")).thenReturn(true);

        // Act
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("Password123");

        // Assert
        AccountLockedException exception = assertThrows(AccountLockedException.class, () -> {
            userService.login(request);
        });
        assertEquals("Tài khoản của bạn đã bị khóa", exception.getMessage());
    }

    @Test
    void registerSuccess() {
        // Arrange
        User user = createActiveUser();
        when(userRepository.existsByEmail("student@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("student01")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashedPassword");

        // Act
        RegisterRequest request = new RegisterRequest();
        request.setEmail(user.getEmail());
        request.setUsername(user.getUsername());
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");
        RegisterResponse response = userService.register(request);

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertNotNull(response);
        assertEquals("student01", response.getUsername());
        assertEquals("student@gmail.com", response.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());

        // Verify
        verify(passwordEncoder).encode("Password123");
    }

    @Test
    void registerExistEmail() {
        // Arrange
        User user = createActiveUser();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act
        RegisterRequest request = new RegisterRequest();
        request.setEmail(user.getEmail());
        request.setUsername(user.getUsername());
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        // Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            RegisterResponse response = userService.register(request);
        });
        assertEquals("Email đã được dùng", exception.getMessage());

        // Verify
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerExistUsername() {
        // Arrange
        User user = createActiveUser();
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        // Act
        RegisterRequest request = new RegisterRequest();
        request.setEmail(user.getEmail());
        request.setUsername(user.getUsername());
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        // Assert
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class, () -> {
            RegisterResponse response = userService.register(request);
        });
        assertEquals("Username đã được dùng", exception.getMessage());

        // Verify
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void passwordMismatch() {
        // Arrange
        User user = createActiveUser();
        RegisterRequest request = new RegisterRequest();
        request.setEmail(user.getEmail());
        request.setUsername(user.getUsername());
        request.setPassword("Password123");
        request.setConfirmPassword("Password12");

        // Act + Assert
        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            RegisterResponse response = userService.register(request);
        });
        assertEquals("Password không khớp với PasswordConfirm", exception.getMessage());

        //
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
