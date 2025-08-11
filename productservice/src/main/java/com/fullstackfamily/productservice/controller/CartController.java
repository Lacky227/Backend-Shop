package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.dto.CartItemResponse;
import com.fullstackfamily.productservice.service.CartService;
import com.fullstackfamily.productservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AddToCartRequest request) {

        String token = extractToken(authorizationHeader);
        String userEmail = jwtService.extractEmail(token);

        CartItemResponse response = cartService.addOrUpdateItem(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getUserCart(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        String userEmail = jwtService.extractEmail(token);

        List<CartItemResponse> response = cartService.getCartResponsesForUser(userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteItemBySku(
            @PathVariable String sku,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        String email = jwtService.extractEmail(token);

        cartService.deleteAllByEmailAndSku(email, sku);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Невірний токен");
    }
}