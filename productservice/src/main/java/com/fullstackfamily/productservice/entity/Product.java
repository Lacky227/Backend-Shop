package com.fullstackfamily.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String brand;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal oldPrice;

    @Column(nullable = false)
    private Boolean hasdiscount;

    @Column(nullable = false)
    private List<String> image = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "product_sizes",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @MapKeyColumn(name = "size")
    @Column(name = "quantity")
    private Map<String, Integer> sizes = new HashMap<>();

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String season;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String material;
}
