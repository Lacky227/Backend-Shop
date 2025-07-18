package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.config.RabbitMQConfig;
import com.fullstackfamily.notificationservice.dto.EmailRequest;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubReceiverService {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.SUBSCRIPTION_QUEUE_NAME)
    public void receive(String email) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(email);
        emailService.subscribe(emailRequest);
    }
}
