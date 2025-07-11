package com.example.myshop.cart.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long memberId;
    private Long productId;
    private int count;
}
