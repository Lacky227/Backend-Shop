package com.fullstackfamily.authservice.controller;

import com.fullstackfamily.authservice.dto.LoginRequest;
import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизація", description = "Операції реєстрації та входу користувача")
public class AuthController {

    public final UserService userService;

    @Operation(
            summary = "Реєстрація нового користувача",
            description = "Приймає дані для створення нового облікового запису"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Користувача зареєстровано успішно"),
            @ApiResponse(responseCode = "400", description = "Некоректні вхідні дані")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Operation(
            summary = "Вхід користувача",
            description = "Приймає облікові дані та повертає JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успішна авторизація"),
            @ApiResponse(responseCode = "401", description = "Невірний email або пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
}
