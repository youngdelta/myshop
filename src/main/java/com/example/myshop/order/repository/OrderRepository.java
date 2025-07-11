package com.example.myshop.order.repository;

import com.example.myshop.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    @Query("SELECT o FROM Orders o WHERE o.orderDate >= ?1 AND o.orderDate < ?2")
    List<Orders> findOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}