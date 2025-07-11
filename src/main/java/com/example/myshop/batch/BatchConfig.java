package com.example.myshop.batch;

import com.example.myshop.order.Orders;
import com.example.myshop.product.Product;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private static final int CHUNK_SIZE = 10;

    // ========================================================================
    // Product Batch Job
    // ========================================================================

    @Bean
    public Job productJob() {
    	log.info("productJob running... ");
        return new JobBuilder("productJob", jobRepository)
                .start(productStep())
                .build();
    }

    @Bean
    public Step productStep() {
        return new StepBuilder("productStep", jobRepository)
                .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
                .reader(productItemReader())
                .processor(productItemProcessor())
                .writer(productItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Product> productItemReader() {
        return new JpaPagingItemReaderBuilder<Product>()
                .name("productItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT p FROM Product p")
                .build();
    }

    @Bean
    public ItemProcessor<Product, Product> productItemProcessor() {
        return item -> {
            log.info("Processing Product: {} (Price: {}, Stock: {}) ", item.getName(), item.getPrice(), item.getStockQuantity());
            return item; // 변경 없이 그대로 반환
        };
    }

    @Bean
    public ItemWriter<Product> productItemWriter() {
        return items -> {
            for (Product item : items) {
                log.info("Writing Product: {} ", item.getName());
            }
        };
    }

    // ========================================================================
    // Order Batch Job
    // ========================================================================

    @Bean
    public Job orderJob() {
        return new JobBuilder("orderJob", jobRepository)
                .start(orderStep())
                .build();
    }

    @Bean
    public Step orderStep() {
        return new StepBuilder("orderStep", jobRepository)
                .<Orders, Orders>chunk(CHUNK_SIZE, transactionManager)
                .reader(orderItemReader())
                .processor(orderItemProcessor())
                .writer(orderItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Orders> orderItemReader() {
        return new JpaPagingItemReaderBuilder<Orders>()
                .name("orderItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT o FROM Orders o JOIN FETCH o.member JOIN FETCH o.orderItems oi JOIN FETCH oi.product") // N+1 문제 방지
                .build();
    }

    @Bean
    public ItemProcessor<Orders, Orders> orderItemProcessor() {
        return item -> {
            log.info("Processing Order: {} (Member: {}, Status: {}) ", item.getId(), item.getMember().getEmail(), item.getStatus());
            item.getOrderItems().forEach(orderItem -> {
                log.info("  - OrderItem: Product {}, Count {}", orderItem.getProduct().getName(), orderItem.getCount());
            });
            return item; // 변경 없이 그대로 반환
        };
    }

    @Bean
    public ItemWriter<Orders> orderItemWriter() {
        return items -> {
            for (Orders item : items) {
                log.info("Writing Order: {} ", item.getId());
            }
        };
    }
}
