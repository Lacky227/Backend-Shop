package com.fullstackfamily.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenService {
    public static String generateToken(String email, String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] hmacSha256 = new HmacUtils("HmacSHA256", keyBytes).hmac(email);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacSha256);
    }
}
