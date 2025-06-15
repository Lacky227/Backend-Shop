package com.fullstackfamily.notificationservice.controller;

import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(
                    responseCode = "202",
                    description = "Успішно підписано або повторно підписано.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Недійсний email або email вже підписано.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера при відправці email.",
                    content =  @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/sub")
    public ResponseEntity<?> subscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.subscribe(emailRequest);
    }

    @Operation(
            summary = "Відписка від сповіщень",
            description = "Видаляє email зі списку підписників."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Успішно відписано або email не був підписаний.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Недійсний email або email вже відписано.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка сервера при обробці запиту.",
                    content =  @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/unsub")
    public ResponseEntity<?> unsubscribe(@RequestBody EmailRequest emailRequest) {
        return emailService.unsubscribe(emailRequest);
    }
}
