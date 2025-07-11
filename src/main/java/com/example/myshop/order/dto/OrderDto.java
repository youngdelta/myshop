package com.example.myshop.order.dto;

import com.example.myshop.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDto {
    private Long id; // 주문 ID 추가
    private Long memberId;
    private Long productId;
    private int count;
    private OrderStatus status; // 주문 상태 추가
}
