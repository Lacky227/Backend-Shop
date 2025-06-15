package com.fullstackfamily.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запит для реєстрації нового користувача")
public class RegisterRequest {
    @Schema(description = "Ім'я користувача", example = "Олександр")
    private String firstName;

    @Schema(description = "Прізвище користувача", example = "Шевченко")
    private String lastName;

    @Schema(description = "Email-адреса", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "Password123!")
    private String password;
}
