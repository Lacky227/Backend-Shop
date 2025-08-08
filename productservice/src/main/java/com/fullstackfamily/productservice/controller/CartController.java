package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.dto.CartItemResponse;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.entity.CartItem;
import com.fullstackfamily.productservice.service.CartService;
import com.fullstackfamily.productservice.service.JwtService;
import com.fullstackfamily.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    // Тут інжекцію можна спростити за допомогою Lombok @RequiredArgsConstructor
    private final CartService cartService;
    private final ProductService productService;
    private final JwtService jwtService;

    public CartController(CartService cartService, ProductService productService, JwtService jwtService) {
        this.cartService = cartService;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AddToCartRequest request) {

        String token = extractToken(authorizationHeader);
        String userEmail = jwtService.extractEmail(token);

        CartItem cartItem = cartService.addOrUpdateItem(userEmail, request);

        Optional<ProductResponse> productResponse = productService.findProductBySku(cartItem.getSku());

        // Логіку з Optional можна спростити через orElseThrow або map
        if (productResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ProductResponse product = productResponse.get();

        // Формування ResponseEntity краще перенести у сервіс, щоб контролер був "тонким"
        CartItemResponse response = CartItemResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .oldPrice(product.getOldPrice())
                .quantity(cartItem.getQuantity())
                .image(product.getImage().isEmpty() ? null : product.getImage().get(0))
                .category(product.getCategory())
                .brand(product.getBrand())
                .size(cartItem.getSize())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getUserCart(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        String userEmail = jwtService.extractEmail(token);

        List<CartItem> items = cartService.getUserCart(userEmail);

        List<CartItemResponse> response = items.stream().map(cartItem -> {
            Optional<ProductResponse> productResponse = productService.findProductBySku(cartItem.getSku());

            // Тут можна уніфікувати через APIResponse для кращої інтеграції з фронтом
            return productResponse.map(product -> CartItemResponse.builder()
                    .sku(product.getSku())
                    .name(product.getName())
                    .price(product.getPrice())
                    .oldPrice(product.getOldPrice())
                    .quantity(cartItem.getQuantity())
                    .image(product.getImage().isEmpty() ? null : product.getImage().get(0))
                    .category(product.getCategory())
                    .brand(product.getBrand())
                    .size(cartItem.getSize())
                    .build()).orElse(null);
        }).filter(Objects::nonNull).toList();

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
