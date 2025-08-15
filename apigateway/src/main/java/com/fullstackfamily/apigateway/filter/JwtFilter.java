package com.fullstackfamily.apigateway.filter;

import com.fullstackfamily.apigateway.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter implements GatewayFilter {
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            log.warn("JWT authentication failed (missing Authorization header) for: {}", request.getPath());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            log.warn("JWT authentication failed (invalid header format) for: {}", request.getPath());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        try {
            if (!jwtService.isTokenValid(token)){
                log.warn("JWT authentication failed (invalid token) for: {}", request.getPath());
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e){
            log.error("JWT authentication failed (exception) for: {}, reason: {}",
                    exchange.getRequest().getPath(), e.getMessage());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        log.warn("JWT authentication failed, for: {}", exchange.getRequest().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}
