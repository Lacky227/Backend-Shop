package com.fullstackfamily.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ProductInfoRequest {
    private String sku;

    private String name;

    private String brand;

    private String gender;

    private String category;

    private BigDecimal price;

    private BigDecimal oldPrice;

    private Boolean hasdiscount;

    private List<String> image;

    private Map<String, Integer> sizes;

    private String color;

    private String season;

    private String description;

    private String material;
}
