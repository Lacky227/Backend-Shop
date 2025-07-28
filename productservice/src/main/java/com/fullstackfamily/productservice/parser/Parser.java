package com.fullstackfamily.productservice.parser;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Parser {
    public String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String id = parts[parts.length - 1];
        return id.substring(0, id.lastIndexOf("."));
    }
}
