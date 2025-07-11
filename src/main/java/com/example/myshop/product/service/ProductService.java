package com.example.myshop.product.service;

import com.example.myshop.product.Product;
import com.example.myshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long saveProduct(Product product) {
        productRepository.save(product);
        return product.getId();
    }

    public List<Product> findProducts() {
        return productRepository.findAll();
    }

    public Product findOne(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
