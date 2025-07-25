package com.fullstackfamily.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductFilterRequest {
    private List<String> category;
    private BigDecimal price_from;
    private BigDecimal price_to;
    private List<String> gender;
    private List<String> color;
    private List<String> season;
    private List<String> size;
    private List<String> brand;
    private List<String> material;
}
