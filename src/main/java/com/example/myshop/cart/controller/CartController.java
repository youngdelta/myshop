package com.example.myshop.cart.controller;

import com.example.myshop.cart.Cart;
import com.example.myshop.cart.CartItem;
import com.example.myshop.cart.dto.AddToCartRequest;
import com.example.myshop.cart.dto.CartDto;
import com.example.myshop.cart.dto.CartItemDto;
import com.example.myshop.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public Long addProductToCart(@RequestBody AddToCartRequest request) {
        return cartService.addProductToCart(request.getMemberId(), request.getProductId(), request.getCount());
    }

    @GetMapping("/{memberId}")
    public CartDto getCart(@PathVariable("memberId") Long memberId) {
        Cart cart = cartService.getCartByMemberId(memberId);
        if (cart == null) {
            return new CartDto(memberId, List.of());
        }
        List<CartItemDto> cartItemDtos = cart.getCartItems().stream()
                .map(item -> new CartItemDto(item.getId(), item.getProduct().getId(), item.getProduct().getName(), item.getCount(), item.getProduct().getPrice()))
                .collect(Collectors.toList());
        return new CartDto(memberId, cartItemDtos);
    }

    @DeleteMapping("/item/{cartItemId}")
    public void removeCartItem(@PathVariable("cartItemId") Long cartItemId) {
        cartService.removeCartItem(cartItemId);
    }

    @DeleteMapping("/{memberId}")
    public void clearCart(@PathVariable("memberId") Long memberId) {
        cartService.clearCart(memberId);
    }

    @GetMapping("/{memberId}/export-excel")
    public ResponseEntity<byte[]> exportCartExcel(@PathVariable("memberId") Long memberId) {
        try {
            byte[] excelBytes = cartService.exportCartToExcel(memberId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "cart.xlsx");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // IOException 발생 시 500 Internal Server Error와 함께 메시지 전달
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 발생 시 400 Bad Request와 함께 메시지 전달
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.BAD_REQUEST);
        }
    }
}
