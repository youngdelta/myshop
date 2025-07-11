package com.example.myshop.order.service;

import com.example.myshop.member.Member;
import com.example.myshop.member.repository.MemberRepository;
import com.example.myshop.order.OrderItem;
import com.example.myshop.order.OrderStatus;
import com.example.myshop.order.Orders;
import com.example.myshop.order.dto.OrderStatisticsDto;
import com.example.myshop.order.repository.OrderRepository;
import com.example.myshop.product.Product;
import com.example.myshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long order(Long memberId, Long productId, int count) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Orders 객체 생성
        Orders order = new Orders();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);

        // OrderItem 객체 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderPrice(product.getPrice());
        orderItem.setCount(count);

        // 양방향 연관관계 편의 메서드 사용
        order.addOrderItem(orderItem);

        orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(OrderStatus.CANCEL);
    }

    public List<Orders> findOrders() {
        return orderRepository.findAll();
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public List<OrderStatisticsDto> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Orders> orders = orderRepository.findOrdersBetweenDates(startDateTime, endDateTime);

        Map<LocalDate, List<Orders>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        return ordersByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Orders> dailyOrders = entry.getValue();

                    long totalOrders = dailyOrders.size();
                    long totalCancellations = dailyOrders.stream()
                            .filter(order -> order.getStatus() == OrderStatus.CANCEL)
                            .count();
                    long totalRevenue = dailyOrders.stream()
                            .filter(order -> order.getStatus() == OrderStatus.ORDER) // 취소되지 않은 주문만 매출에 포함
                            .flatMap(order -> order.getOrderItems().stream())
                            .mapToLong(item -> (long) item.getOrderPrice() * item.getCount())
                            .sum();

                    return new OrderStatisticsDto(date, totalOrders, totalCancellations, totalRevenue);
                })
                .sorted((s1, s2) -> s1.getOrderDate().compareTo(s2.getOrderDate()))
                .collect(Collectors.toList());
    }
}