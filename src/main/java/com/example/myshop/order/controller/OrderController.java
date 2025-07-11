package com.example.myshop.order.controller;

import com.example.myshop.order.Orders;
import com.example.myshop.order.dto.OrderDto;
import com.example.myshop.order.dto.OrderStatisticsDto;
import com.example.myshop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Long order(@RequestBody OrderDto orderDto) {
        return orderService.order(orderDto.getMemberId(), orderDto.getProductId(), orderDto.getCount());
    }

    @PostMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
    }

    @GetMapping
    public List<OrderDto> list() {
        List<Orders> orders = orderService.findOrders();
        return orders.stream()
                .map(o -> new OrderDto(o.getId(), o.getMember().getId(), o.getOrderItems().get(0).getProduct().getId(), o.getOrderItems().get(0).getCount(), o.getStatus())) // status 추가
                .collect(Collectors.toList());
    }

    @GetMapping("/statistics")
    public List<OrderStatisticsDto> getOrderStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return orderService.getDailyOrderStatistics(startDate, endDate);
    }
}