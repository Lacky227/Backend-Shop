package com.fullstackfamily.authservice.service;

import com.fullstackfamily.authservice.config.RabbitMQConfig;
import com.fullstackfamily.authservice.dto.ForgotRequest;
import com.fullstackfamily.authservice.dto.SubSend;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubServiceSender {
    private final RabbitTemplate rabbitTemplate;

    public void send(SubSend subResponse) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.SUBSCRIPTION_ROUTING_KEY, subResponse.getEmail());
    }

    public void sendForgotPassword(ForgotRequest request){
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.RESET_ROUTING_KEY, request.getEmail());
    }
}
