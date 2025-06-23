package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.entity.Product;
import com.fullstackfamily.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponses = products.stream()
                .map(e -> new ProductResponse(
                        e.getSku(),
                        e.getName(),
                        e.getBrand(),
                        e.getGender(),
                        e.getCategory(),
                        e.getPrice(),
                        e.getOldPrice(),
                        e.getHasdiscount(),
                        e.getImage(),
                        e.getSizes(),
                        e.getColor(),
                        e.getSeason(),
                        e.getDescription(),
                        e.getMaterial()))
                .toList();
        return ResponseEntity.ok(productResponses);
    }
}
