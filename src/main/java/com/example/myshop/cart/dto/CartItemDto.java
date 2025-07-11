package com.example.myshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int count;
    private int price;
}
