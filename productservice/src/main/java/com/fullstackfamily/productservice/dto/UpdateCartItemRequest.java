package com.fullstackfamily.productservice.dto;

import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private Integer quantity;
    private String size;
}
