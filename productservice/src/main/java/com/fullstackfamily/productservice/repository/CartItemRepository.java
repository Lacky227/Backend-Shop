package com.fullstackfamily.productservice.repository;

import com.fullstackfamily.productservice.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {

    Optional<CartItem> findByEmailAndSkuAndSize(String email, String sku, String size);
    List<CartItem> findAllByEmail(String email);
    List<CartItem> findAllByEmailAndSku(String email, String sku);
    void deleteByEmailAndSku(String email, String sku);
}
