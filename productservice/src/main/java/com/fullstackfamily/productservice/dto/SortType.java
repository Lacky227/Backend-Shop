package com.fullstackfamily.productservice.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SortType {
    PRICE_ASC("price-asc"),
    PRICE_DESC("price-desc"),
    NEW("new"),
    POPULAR("popular"),
    DISCOUNT("discount");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public static Optional<SortType> fromValue(String value) {
        return Arrays.stream(SortType.values())
                .filter(v -> v.getValue().equalsIgnoreCase(value))
                .findFirst();
    }
}
