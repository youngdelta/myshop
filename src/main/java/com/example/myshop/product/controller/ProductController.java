package com.example.myshop.product.controller;

import com.example.myshop.product.Product;
import com.example.myshop.product.dto.ProductDto;
import com.example.myshop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto saveProduct(@RequestBody ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        productService.saveProduct(product);
        return new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getStockQuantity()); // 저장 후 ID 포함하여 반환
    }

    @GetMapping
    public List<ProductDto> list() {
        List<Product> products = productService.findProducts();
        return products.stream()
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStockQuantity())) // id를 productId로 매핑
                .collect(Collectors.toList());
    }
}
