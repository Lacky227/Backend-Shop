package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.AddToCartRequest;
import com.fullstackfamily.productservice.dto.CartItemResponse;
import com.fullstackfamily.productservice.dto.UpdateCartItemRequest;
import com.fullstackfamily.productservice.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Кошик", description = "Операції додавання, оновлення, перегляду та видалення товарів у кошику")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Додати товар до кошика",
            description = "Додає товар до кошика користувача або оновлює кількість, якщо такий товар вже є"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успішно додано/оновлено", content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні дані", content = @Content),
            @ApiResponse(responseCode = "500", description = "Помилка сервера", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-Email") String email,
            @RequestBody AddToCartRequest request) {

        CartItemResponse response = cartService.addOrUpdateItem(email, request);
        return ResponseEntity.ok("Товар добавлено в кошик");
    }

    @Operation(
            summary = "Отримати кошик користувача",
            description = "Повертає список усіх товарів у кошику користувача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товарів у кошику", content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Помилка сервера", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getUserCart(
            @RequestHeader("X-User-Email") String email) {

        List<CartItemResponse> response = cartService.getCartResponsesForUser(email);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Оновити товар у кошику",
            description = """
                    Змінює кількість або розмір товару в кошику за SKU.
                    Якщо розмір змінено і такий товар уже є — об'єднує кількість.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успішно оновлено", content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Товар не знайдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Помилка сервера", content = @Content)
    })
    @PatchMapping("/{sku}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable String sku,
            @RequestBody UpdateCartItemRequest request,
            @RequestHeader("X-User-Email") String email) {

        // TODO: Повернути повідомлення
        CartItemResponse response = cartService.updateItem(email, sku, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Видалити товар з кошика",
            description = "Видаляє товар із кошика за SKU користувача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Товар видалено"),
            @ApiResponse(responseCode = "404", description = "Товар не знайдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Помилка сервера", content = @Content)
    })
    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteItemBySku(
            @PathVariable String sku,
            @RequestHeader("X-User-Email") String email) {

        cartService.deleteAllByEmailAndSku(email, sku);
        // TODO: Повернути повідомлення
        return ResponseEntity.noContent().build();
    }
}