package com.fullstackfamily.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Data
public class UpdateProductRequest {
    private Optional<String> sku;

    private Optional<String> name;

    private Optional<String> brand;

    private Optional<String> gender;

    private Optional<String> category;

    private Optional<BigDecimal> price;

    private Optional<BigDecimal> oldPrice;

    private Optional<Boolean> hasdiscount;

    private Optional<Boolean> newCollection;

    private Optional<Boolean> topSales;

    private Optional<Map<String, Integer>> sizes;

    private Optional<String> color;

    private Optional<String> season;

    private Optional<String> description;

    private Optional<String> material;
}
