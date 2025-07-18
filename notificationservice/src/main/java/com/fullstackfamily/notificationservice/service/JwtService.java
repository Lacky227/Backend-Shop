package com.fullstackfamily.notificationservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${secret.key}")
    private String SECRET_KEY;
    private static final long UNSUBSCRIBE_TOKEN_VALIDITY = 0;
    private static final long FORGOT_PASSWORD_TOKEN_VALIDITY = 15 * 60 * 1000;

    public String generateToken(String email, boolean isExpirable) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(isExpirable ? new Date(System.currentTimeMillis() + FORGOT_PASSWORD_TOKEN_VALIDITY) : null)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenUnsubscribe(String email) {
        return generateToken(email, false);
    }

    public String generateTokenForgotPassword(String email) {
        return generateToken(email, true);
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);

            // Перевірка терміну дії, якщо він заданий
            Date expiration = claims.getBody().getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
