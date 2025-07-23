package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.*;
import com.fullstackfamily.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Управління продуктами", description = "REST-контролер для створення, перегляду та видалення продуктів.")
@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Отримати всі продукти",
            description = "Повертає список усіх продуктів, доступних у системі."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Успішне отримання списку продуктів",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "Помилка сервера",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam Optional<String> sort) {
        Optional<SortType> sortType = sort.flatMap(SortType::fromValue);
        return productService.getAllProducts(sortType);
    }

    @Operation(
            summary = "Отримати продукт за SKU",
            description = "Повертає детальну інформацію про продукт за заданим SKU."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Продукт знайдено",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Продукт не знайдено",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "Помилка сервера",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }

    @Operation(
            summary = "Створити новий продукт",
            description = "Створює новий продукт з переданими параметрами."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Продукт успішно створено",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Некоректні вхідні дані",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "Помилка сервера",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody ProductInfoRequest request) {
        return productService.createProduct(request);
    }

    @PatchMapping("/update/{sku}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable String sku, @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(sku, request);
    }

    @Operation(
            summary = "Видалити продукт за SKU",
            description = "Видаляє продукт із системи за вказаним SKU."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Продукт успішно видалено",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Продукт не знайдено",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "Помилка сервера",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String sku) {
        return productService.deleteProduct(sku);
    }
}
