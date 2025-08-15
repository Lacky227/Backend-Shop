package com.fullstackfamily.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AdminFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!request.getHeaders().containsKey("X-User-Role")) {
            log.warn("Admin access denied (missing X-User-Role header) for: {}", request.getPath());
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        String role = request.getHeaders().getFirst("X-User-Role");
        if (role == null || !role.equalsIgnoreCase("ROLE_ADMIN")) {
            log.warn("Admin access denied (role is '{}') for: {}", role, request.getPath());
            return onError(exchange, HttpStatus.FORBIDDEN);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        log.warn("Admin filter blocked request to: {}", exchange.getRequest().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}
