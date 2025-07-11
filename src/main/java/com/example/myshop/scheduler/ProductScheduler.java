package com.example.myshop.scheduler;

import com.example.myshop.product.Product;
import com.example.myshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j // @Slf4j 어노테이션 추가
public class ProductScheduler {

    private final ProductService productService;

    // 1분(60000ms)마다 실행
    @Scheduled(fixedRate = 60000)
    public void fetchProductList() {
        List<Product> products = productService.findProducts();
        log.info("[" + LocalDateTime.now() + "] Scheduled task: Fetched " + products.size() + " products."); // log.info 사용
        products.forEach(product -> {
            log.info("  - Product: {}, Price: {}, Stock: {}", product.getName(), product.getPrice(), product.getStockQuantity()); // log.info 사용
        });
    }
}
