package com.fullstackfamily.productservice.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private String sku;
    private int quantity;
    private String size;
}
