package com.example.post.test.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
