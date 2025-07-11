package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.ApiResponse;
import com.fullstackfamily.productservice.dto.ProductInfoRequest;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody ProductInfoRequest request){
        return productService.createProduct(request);
    }
    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String sku){
        return productService.deleteProduct(sku);
    }
}
