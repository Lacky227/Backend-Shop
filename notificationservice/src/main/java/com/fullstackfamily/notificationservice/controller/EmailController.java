package com.fullstackfamily.notificationservice.controller;

import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@AllArgsConstructor
@Tag(name = "Підписатись/Відписатись", description = "REST-контролер для підписки та відписки від email-повідомлень.")
public class EmailController {
    private EmailService emailService;

    @Operation(
            summary = "Підписка на сповіщення",
            description = "Додає email до списку підписників для отримання сповіщень і відправляє на вказаний email повідомлення."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Успішно підписано або повторно підписано."),
            @ApiResponse(responseCode = "400", description = "Недійсний email або email вже підписано."),
            @ApiResponse(responseCode = "500", description = "Помилка сервера при відправці email.")
    })
    @PostMapping("/sub")
    public ResponseEntity<String> subscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.subscribe(emailRequest);
    }

    @Operation(
            summary = "Відписка від сповіщень",
            description = "Видаляє email зі списку підписників."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Успішно відписано або email не був підписаний."),
            @ApiResponse(responseCode = "400", description = "Недійсний email або email вже відписано."),
            @ApiResponse(responseCode = "500", description = "Помилка сервера при обробці запиту.")
    })
    @PostMapping("/unsub")
    public ResponseEntity<String> unsubscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.unsubscribe(emailRequest);
    }
}
