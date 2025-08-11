package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.dto.CartItemResponse;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.entity.CartItem;
import com.fullstackfamily.productservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;

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

        return CartItemResponse.builder()
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
    }

    public List<CartItemResponse> getCartResponsesForUser(String email) {
        return cartItemRepository.findAllByEmail(email).stream()
                .map(cartItem -> productService.findProductBySku(cartItem.getSku())
                        .map(product -> CartItemResponse.builder()
                                .sku(product.getSku())
                                .name(product.getName())
                                .price(product.getPrice())
                                .oldPrice(product.getOldPrice())
                                .quantity(cartItem.getQuantity())
                                .image(product.getImage().isEmpty() ? null : product.getImage().get(0))
                                .category(product.getCategory())
                                .brand(product.getBrand())
                                .size(cartItem.getSize())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public void deleteAllByEmailAndSku(String email, String sku) {
        cartItemRepository.deleteByEmailAndSku(email, sku);
    }

    private CartItem createNewItem(String email, AddToCartRequest request) {
        CartItem newItem = new CartItem();
        newItem.setEmail(email);
        newItem.setSku(request.getSku());
        newItem.setSize(request.getSize());
        newItem.setQuantity(request.getQuantity());
        return cartItemRepository.save(newItem);
    }
}