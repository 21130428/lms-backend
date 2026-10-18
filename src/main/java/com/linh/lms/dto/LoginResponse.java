package com.linh.lms.dto;

import com.linh.lms.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String token;
    private String refreshToken;

    public LoginResponse(Long id, String username, String email, Role role, String token, String refreshToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
