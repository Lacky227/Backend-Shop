package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.*;
import com.fullstackfamily.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "Управління продуктами", description = "Операції створення, перегляду, оновлення та видалення продуктів")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Отримати всі продукти",
            description = "Повертає список усіх продуктів, з можливістю сортування"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успішне отримання списку продуктів",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутрішня помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Помилка при отриманні продуктів\" }")
                    )
            )
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(@ModelAttribute ProductFilterRequest filter, @RequestParam Optional<String> sort) {
        Optional<SortType> sortType = sort.flatMap(SortType::fromValue);
        return productService.getAllProducts(filter, sortType);
    }

    @Operation(
            summary = "Отримати продукт за SKU",
            description = "Повертає продукт за вказаним SKU"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт знайдено",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не знайдено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Продукт не знайдено.\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутрішня помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Помилка при пошуку продукту.\" }")
                    )
            )
    })
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }

    @Operation(
            summary = "Створити новий продукт",
            description = "Створює продукт на основі переданих даних"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Продукт успішно створено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Товар успішно створений\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні вхідні дані",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Всі обов’язкові поля мають бути заповнені\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "SKU вже існує",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Товар із цим sku вже є\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Внутрішня помилка сервера\" }")
                    )
            )
    })
    @PostMapping("/create")
    public ResponseEntity<APIResponse> createProduct(@RequestBody ProductInfoRequest request) {
        return productService.createProduct(request);
    }

    @Operation(
            summary = "Оновити продукт",
            description = "Оновлює властивості продукту за SKU. Передайте лише ті поля, які потрібно змінити."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт успішно оновлено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Товар успішно оновлено\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не знайдено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Товар не знайдено\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутрішня помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Сталася помилка при оновленні\" }")
                    )
            )
    })
    @PatchMapping("/update/{sku}")
    public ResponseEntity<APIResponse> updateProduct(@PathVariable String sku, @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(sku, request);
    }

    @Operation(
            summary = "Видалити продукт",
            description = "Видаляє продукт із системи за вказаним SKU"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт успішно видалено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Товар видалено\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не знайдено",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Не знайдено товар\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Сталася помилка при видаленні\" }")
                    )
            )
    })
    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<APIResponse> deleteProduct(@PathVariable String sku) {
        return productService.deleteProduct(sku);
    }
}
