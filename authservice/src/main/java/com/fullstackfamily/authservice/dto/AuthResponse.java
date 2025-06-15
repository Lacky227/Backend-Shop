package com.fullstackfamily.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Відповідь на успішну авторизацію")
public class AuthResponse {
    @Schema(description = "JWT-токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Роль користувача", example = "ROLE_USER")
    private String role;
}
