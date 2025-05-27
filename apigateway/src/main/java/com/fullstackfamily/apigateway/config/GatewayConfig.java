package com.fullstackfamily.apigateway.config;

import com.fullstackfamily.apigateway.filter.JwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class GatewayConfig {
    private final JwtFilter jwtFilter;

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                                .path("/auth/**")
                                .uri("http://localhost:8081/"))
                .build();
    }
}
