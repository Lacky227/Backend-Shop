package com.fullstackfamily.productservice.repository;

import com.fullstackfamily.productservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByEmailAndSkuAndSize(String userEmail, String sku, String size);
    List<CartItem> findAllByEmail(String userEmail);
    List<CartItem> findAllByEmailAndSku(String email, String sku);
    void deleteByEmailAndSku(String email, String sku);
}
