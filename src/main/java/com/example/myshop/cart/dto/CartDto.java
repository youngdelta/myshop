package com.example.myshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {
    private Long memberId;
    private List<CartItemDto> cartItems;
}
