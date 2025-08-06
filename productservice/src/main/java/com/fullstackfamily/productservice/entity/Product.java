package com.fullstackfamily.productservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Product entity representing items in the store")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Stock Keeping Unit (SKU)", example = "SKU12345")
    private String sku;

    @Column(nullable = false)
    @Schema(description = "Product name", example = "Nike Air Max")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    @Schema(description = "Brand of the product", example = "Nike")
    private Brand brand;

    @Column(nullable = false)
    @Schema(description = "Gender category", example = "Men")
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Product category", example = "Shoes")
    private Category category;

    @Column(nullable = false)
    @Schema(description = "Current price of the product", example = "129.99")
    private BigDecimal price;

    @Schema(description = "Old price if discounted", example = "149.99")
    private BigDecimal oldPrice;

    @Column(nullable = false)
    @Schema(description = "Whether the product has a discount", example = "true")
    private Boolean hasdiscount;

    @Schema(description = "", example = "true/false")
    private Boolean newCollection;

    @Schema(description = "", example = "true/false")
    private Boolean topSales;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of product images")
    private List<Images> image = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "product_sizes",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @MapKeyColumn(name = "size")
    @Column(name = "quantity")
    @Schema(description = "Available sizes and quantities")
    private Map<String, Integer> sizes = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    @Schema(description = "Color of the product", example = "Black")
    private Color color;

    @Column(nullable = false)
    @Schema(description = "Seasonal tag", example = "Winter")
    private String season;

    @Column(nullable = false, length = 500)
    @Schema(description = "Product description", example = "High-performance winter shoes with water resistance.")
    private String description;

    @Column(nullable = false)
    @Schema(description = "Material used", example = "Leather")
    private String material;
}