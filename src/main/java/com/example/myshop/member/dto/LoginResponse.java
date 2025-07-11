package com.example.myshop.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long memberId;
    private String email;
    private String message;
}
