package com.fullstackfamily.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Data
public class UpdateProductRequest {
    private String sku;
    private String name;
    private String brand;
    private String gender;
    private String category;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private Boolean hasdiscount;
    private Boolean newCollection;
    private Boolean topSales;
    private Map<String, Integer> sizes;
    private String color;
    private String season;
    private String description;
    private String material;
}
