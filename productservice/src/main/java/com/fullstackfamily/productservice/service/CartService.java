package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.entity.CartItem;
import com.fullstackfamily.productservice.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartItem addOrUpdateItem(String userEmail, AddToCartRequest request) {
        Optional<CartItem> existing = cartItemRepository.findByUserEmailAndSkuAndSize(userEmail, request.getSku(), request.getSize());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            return cartItemRepository.save(item);
        }

        CartItem newItem = new CartItem();
        newItem.setEmail(userEmail);
        newItem.setSku(request.getSku());
        newItem.setSize(request.getSize());
        newItem.setQuantity(request.getQuantity());
        return cartItemRepository.save(newItem);
    }

    public List<CartItem> getUserCart(String userEmail) {
        return cartItemRepository.findAllByEmail(userEmail);
    }

    public void deleteAllByEmailAndSku(String email, String sku) {
        List<CartItem> items = cartItemRepository.findAllByEmailAndSku(email, sku);
        if (!items.isEmpty()) {
            cartItemRepository.deleteAll(items);
        }
    }
}
