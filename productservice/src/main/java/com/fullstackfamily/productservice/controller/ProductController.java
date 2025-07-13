package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.ApiResponse;
import com.fullstackfamily.productservice.dto.ProductInfoRequest;
import com.fullstackfamily.productservice.dto.ProductResponse;
import com.fullstackfamily.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Tag(name = "ProductService Controller", description = "Operations related to products")
@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Returns a list of all products available in the system"
    )
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(
            summary = "Get product by SKU",
            description = "Returns details of a product by its SKU"
    )
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product with the given details"
    )
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody ProductInfoRequest request){
        return productService.createProduct(request);
    }

    @Operation(
            summary = "Delete a product by SKU",
            description = "Deletes the product with the specified SKU"
    )
    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String sku){
        return productService.deleteProduct(sku);
    }
}
