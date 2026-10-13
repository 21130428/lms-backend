package com.linh.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String username;
    private String email;

    public RegisterResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
