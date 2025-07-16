package com.fullstackfamily.notificationservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "auth-to-notif";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }
}
