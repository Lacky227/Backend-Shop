package com.fullstackfamily.apigateway.config;

import com.fullstackfamily.apigateway.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    JwtService jwtService() {
        return new JwtService();
    }
}
