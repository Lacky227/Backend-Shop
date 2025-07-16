package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.config.RabbitMQConfig;
import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.dto.SubReceive;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubReceiverService {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receive(SubReceive subReceive) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(subReceive.getEmail());
        emailService.subscribe(emailRequest);
    }
}
