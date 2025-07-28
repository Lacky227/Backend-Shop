package com.fullstackfamily.productservice.dto;

import lombok.Data;

@Data
public class DeleteImageRequest {
    private String fileName;
    private String productSku;
}
