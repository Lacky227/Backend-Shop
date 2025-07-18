package com.fullstackfamily.authservice.controller;

import com.fullstackfamily.authservice.dto.AuthResponse;
import com.fullstackfamily.authservice.dto.ForgotRequest;
import com.fullstackfamily.authservice.dto.LoginRequest;
import com.fullstackfamily.authservice.dto.RegisterRequest;
import com.fullstackfamily.authservice.service.UserService;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизація", description = "Операції реєстрації та входу користувача")
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "Реєстрація нового користувача",
            description = "Створює новий обліковий запис із зазначеними даними користувача."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Користувача успішно зареєстровано",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні вхідні дані",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Некоректні вхідні дані (недійсне ім’я, прізвище, email або пароль)\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email уже використовується",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Користувач з таким email вже існує.\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Сталася внутрішня помилка сервера.\" }")
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Operation(
            summary = "Вхід користувача",
            description = "Автентифікує користувача за email та паролем, повертає JWT-токен і роль."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успішна авторизація",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Невірний email або пароль",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Невірний email або пароль.\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Сталася внутрішня помилка сервера.\" }")
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @Operation(
            summary = "Відновлення паролю",
            description = "Надсилає лист для відновлення паролю, якщо email зареєстровано у системі."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Лист для відновлення паролю успішно надіслано",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Повідомлення для підтвердження надіслано.\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректний email",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Недійсний email. Введіть коректну адресу електронної пошти.\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутрішня помилка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = "{ \"message\": \"Сталася внутрішня помилка сервера.\" }")
                    )
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotRequest request) {
        return userService.forgotPassword(request);
    }
}
