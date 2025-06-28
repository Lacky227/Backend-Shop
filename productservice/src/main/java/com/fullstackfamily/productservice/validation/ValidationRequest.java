package com.fullstackfamily.productservice.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationRequest {
    public boolean isNullOrEmpty(String value){
        return value == null || value.trim().isEmpty();
    }
}
