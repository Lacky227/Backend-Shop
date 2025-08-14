package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.dto.CartItemResponse;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.dto.UpdateCartItemRequest;
import com.fullstackfamily.productservice.entity.CartItem;
import com.fullstackfamily.productservice.repository.CartItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartItemResponse addOrUpdateItem(String userEmail, AddToCartRequest request) {
        CartItem cartItem = cartItemRepository.findByEmailAndSkuAndSize(userEmail, request.getSku(), request.getSize())
                .map(item -> {
                    item.setQuantity(item.getQuantity() + request.getQuantity());
                    return cartItemRepository.save(item);
                })
                .orElseGet(() -> createNewItem(userEmail, request));

        ProductResponse product = productService.findProductBySku(cartItem.getSku())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не знайдено"));

        return buildCartItemResponse(cartItem, product);
    }

    public List<CartItemResponse> getCartResponsesForUser(String email) {
        return cartItemRepository.findAllByEmail(email).stream()
                .map(cartItem -> productService.findProductBySku(cartItem.getSku())
                        .map(product -> buildCartItemResponse(cartItem, product))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public CartItemResponse updateItem(String email, String sku, UpdateCartItemRequest request) {
        List<CartItem> itemsBySku = cartItemRepository.findAllByEmailAndSku(email, sku);

        // ToDo

        return null;
    }

    public void deleteAllByEmailAndSku(String email, String sku) {
        cartItemRepository.deleteByEmailAndSku(email, sku);
    }

    private CartItem createNewItem(String email, AddToCartRequest request) {
        return cartItemRepository.save(CartItem.builder()
                .email(email)
                .sku(request.getSku())
                .size(request.getSize())
                .quantity(request.getQuantity())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private CartItemResponse buildCartItemResponse(CartItem cartItem, ProductResponse product) {
        return CartItemResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .oldPrice(product.getOldPrice())
                .quantity(cartItem.getQuantity())
                .image(product.getImage().isEmpty() ? null : product.getImage().getFirst())
                .category(product.getCategory())
                .brand(product.getBrand())
                .size(cartItem.getSize())
                .build();
    }
}