package com.fullstackfamily.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Універсальна відповідь API з повідомленням про результат операції")
public class APIResponse {
    @Schema(
            description = "Текстове повідомлення про результат виконання запиту",
            example = "Операція виконана успішно"
    )
    private String message;
}
