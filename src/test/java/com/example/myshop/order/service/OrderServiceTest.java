package com.example.myshop.order.service;

import com.example.myshop.member.Member;
import com.example.myshop.member.repository.MemberRepository;
import com.example.myshop.order.OrderItem;
import com.example.myshop.order.OrderStatus;
import com.example.myshop.order.Orders;
import com.example.myshop.order.repository.OrderRepository;
import com.example.myshop.product.Product;
import com.example.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ProductRepository productRepository;

    private Member member;
    private Product product;
    private Orders order;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(10000);
        product.setStockQuantity(10);

        order = new Orders();
        order.setId(1L);
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setOrderPrice(product.getPrice());
        orderItem.setCount(1);
        order.addOrderItem(orderItem);
    }

    @Test
    void cancelOrder_success() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        // When
        orderService.cancelOrder(order.getId());

        // Then
        assertEquals(OrderStatus.CANCEL, order.getStatus());
    }

    @Test
    void cancelOrder_orderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.cancelOrder(999L) // 존재하지 않는 ID
        );
        assertEquals("Order not found", exception.getMessage());
    }
}
