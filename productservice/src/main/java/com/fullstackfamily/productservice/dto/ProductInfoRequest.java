package com.fullstackfamily.productservice.dto;

import com.fullstackfamily.productservice.entity.Images;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class ProductInfoRequest {
    private String sku;

    private String name;

    private Long brandId;

    private String gender;

    private Long categoryId;

    private BigDecimal price;

    private BigDecimal oldPrice;

    private Boolean hasdiscount;

    private Boolean newCollection;

    private Boolean topSales;

    private Map<String, Integer> sizes;

    private Long colorId;

    private String season;

    private String description;

    private String material;
}
