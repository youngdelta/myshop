package com.example.myshop.cart.service;

import com.example.myshop.cart.Cart;
import com.example.myshop.cart.CartItem;
import com.example.myshop.cart.repository.CartItemRepository;
import com.example.myshop.cart.repository.CartRepository;
import com.example.myshop.member.Member;
import com.example.myshop.member.repository.MemberRepository;
import com.example.myshop.product.Product;
import com.example.myshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addProductToCart(Long memberId, Long productId, int count) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Cart cart = cartRepository.findByMemberId(memberId);
        if (cart == null) {
            cart = new Cart();
            cart.setMember(member);
            cart = cartRepository.save(cart); // <--- 이 부분 수정: 저장 후 반환된 엔티티를 다시 할당
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setCount(count);
            cart.addCartItem(cartItem);
        } else {
            cartItem.setCount(cartItem.getCount() + count);
        }
        cartItemRepository.save(cartItem);
        return cart.getId();
    }

    public Cart getCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
        }
    }

    public byte[] exportCartToExcel(Long memberId) throws IOException {
        Cart cart = cartRepository.findByMemberId(memberId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found for memberId: " + memberId);
        }

        ClassPathResource resource = new ClassPathResource("cart_template.xlsx");
        if (!resource.exists()) {
            throw new FileNotFoundException("Excel template file not found: cart_template.xlsx. Please ensure it's in src/main/resources.");
        }

        try (InputStream is = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 사용

            // 헤더가 이미 템플릿에 있다고 가정하고, 데이터는 2번째 행부터 시작 (0-indexed: 1)
            int rowNum = 1;
            for (CartItem item : cart.getCartItems()) {
                Row row = sheet.createRow(rowNum++);
                // Null 체크 추가
                String productName = (item.getProduct() != null) ? item.getProduct().getName() : "N/A";
                double productPrice = (item.getProduct() != null) ? item.getProduct().getPrice() : 0;

                row.createCell(0).setCellValue(productName); // Product Name
                row.createCell(1).setCellValue(productPrice); // Price
                row.createCell(2).setCellValue(item.getCount()); // Count
                row.createCell(3).setCellValue(productPrice * item.getCount()); // Total
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            // 더 자세한 예외 메시지 전달
            throw new IOException("Error processing Excel file for cart export: " + e.getMessage(), e);
        } catch (Exception e) { // POI 관련 다른 예외를 잡기 위함
            throw new IOException("An unexpected error occurred during Excel export: " + e.getMessage(), e);
        }
    }
}