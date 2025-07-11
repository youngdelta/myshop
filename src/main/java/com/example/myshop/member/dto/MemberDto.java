package com.example.myshop.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String password;
    private String city;
    private String street;
    private String zipcode;
}
