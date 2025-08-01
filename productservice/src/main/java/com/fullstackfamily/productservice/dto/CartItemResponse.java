package com.fullstackfamily.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponse {
    private String sku;
    private String name;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private int quantity;
    private String image;
    private String category;
    private String brand;
    private String size;
}