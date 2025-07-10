package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.ApiResponse;
import com.fullstackfamily.productservice.dto.ProductInfoRequest;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.entity.Images;
import com.fullstackfamily.productservice.entity.Product;
import com.fullstackfamily.productservice.repository.ProductRepository;
import com.fullstackfamily.productservice.validation.ValidationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                        e.getImage().stream().map(Images::getNewUrl).toList(),
                        e.getSizes(),
                        e.getColor(),
                        e.getSeason(),
                        e.getDescription(),
                        e.getMaterial()))
                .toList();
        return ResponseEntity.ok(productResponses);
    }
    public ResponseEntity<ProductResponse> getProductBySku(String sku) {
        Optional<Product> product = productRepository.findBySku(sku);
        return product.map(value ->
                ResponseEntity.ok(
                        new ProductResponse(
                                value.getSku(),
                                value.getName(),
                                value.getBrand(),
                                value.getGender(),
                                value.getCategory(),
                                value.getPrice(),
                                value.getOldPrice(),
                                value.getHasdiscount(),
                                value.getImage().stream().map(Images::getNewUrl).toList(),
                                value.getSizes(),
                                value.getColor(),
                                value.getSeason(),
                                value.getDescription(),
                                value.getMaterial())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    public ResponseEntity<ApiResponse> createProduct(ProductInfoRequest request) {
        Optional<Product> productBySku = productRepository.findBySku(request.getSku());
        if (productBySku.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Товар із цим sku вже є"));
        } else if (ValidationRequest.isNullOrEmpty(request.getName()) ||
                ValidationRequest.isNullOrEmpty(request.getBrand()) ||
                ValidationRequest.isNullOrEmpty(request.getGender()) ||
                ValidationRequest.isNullOrEmpty(request.getCategory()) ||
                request.getPrice() == null ||
                request.getSizes() == null || request.getSizes().isEmpty() ||
                ValidationRequest.isNullOrEmpty(request.getColor()) ||
                ValidationRequest.isNullOrEmpty(request.getSeason()) ||
                ValidationRequest.isNullOrEmpty(request.getDescription()) ||
                ValidationRequest.isNullOrEmpty(request.getMaterial())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Всі обов’язкові поля мають бути заповнені"));
        }
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setGender(request.getGender());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setOldPrice(request.getOldPrice());
        product.setHasdiscount(request.getHasdiscount());
        product.setSizes(request.getSizes());
        product.setColor(request.getColor());
        product.setSeason(request.getSeason());
        product.setDescription(request.getDescription());
        product.setMaterial(request.getMaterial());
        productRepository.save(product);
        return ResponseEntity.ok(new ApiResponse("Товар успішно створений"));
    }
}
