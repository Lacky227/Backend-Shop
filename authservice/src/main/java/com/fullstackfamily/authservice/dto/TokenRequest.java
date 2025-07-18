package com.fullstackfamily.authservice.dto;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class TokenRequest {
    private String token;
    private String newPassword;
}
