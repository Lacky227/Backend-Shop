package com.fullstackfamily.notificationservice.controller;

import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@AllArgsConstructor
@CrossOrigin("*")
public class EmailController {
    private EmailService emailService;

    @PostMapping("/sub")
    public ResponseEntity<?> subscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.subscribe(emailRequest);
    }
    @PostMapping("/unsub")
    public ResponseEntity<?> unsubscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.unsubscribe(emailRequest);
    }
}
