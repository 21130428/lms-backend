package com.linh.lms.dto;

import com.linh.lms.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String address;
    private String phone;
    private Role role;

    public UserResponse(Long id, String username, String email, String address, String phone, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }
}
