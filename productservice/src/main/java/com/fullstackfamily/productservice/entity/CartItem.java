package com.fullstackfamily.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String sku;

    private int quantity;

    private String size;

    private LocalDateTime createdAt = LocalDateTime.now();

}
