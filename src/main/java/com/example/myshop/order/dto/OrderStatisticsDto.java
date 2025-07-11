package com.example.myshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OrderStatisticsDto {
    private LocalDate orderDate;
    private long totalOrders;
    private long totalCancellations;
    private long totalRevenue;
}
