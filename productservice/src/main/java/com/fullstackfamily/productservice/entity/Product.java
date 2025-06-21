package com.fullstackfamily.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal oldPrice;

    @Column(nullable = false)
    private Boolean hasDiscount;

    @Column(nullable = false)
    private List<String> images;

    @Column(nullable = false)
    private List<String> sizes;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String season;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String material;
}
