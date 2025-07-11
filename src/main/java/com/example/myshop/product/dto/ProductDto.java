package com.example.myshop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    private String name;
    private int price;
    private int stockQuantity;
}
