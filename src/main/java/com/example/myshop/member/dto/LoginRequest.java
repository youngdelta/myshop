package com.example.myshop.member.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String userId;
    private String password;
}
