package com.fullstackfamily.productservice.validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "product.allowed")
public class ProductValidationProperties {
    private Set<String> categories;
    private Set<String> colors;
    private Set<String> genders;
}