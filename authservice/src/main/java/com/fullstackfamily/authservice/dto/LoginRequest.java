package com.fullstackfamily.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запит для входу користувача")
public class LoginRequest {
    @Schema(description = "Email-адреса", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "Password123!")
    private String password;
}
