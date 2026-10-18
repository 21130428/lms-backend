package com.linh.lms.controller;

import com.linh.lms.dto.UserResponse;
import com.linh.lms.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/me")
    public UserResponse me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getAddress(), user.getPhone(), user.getRole());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "admin endpoint reached";
    }
}
