package com.fullstackfamily.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запит на видалення зображення")
public class DeleteImageRequest {

    @Schema(description = "Назва зображення для видалення", example = "TSH15556-1.jpg")
    private String fileName;

    @Schema(description = "SKU товару, до якого належить зображення", example = "SKU12345")
    private String productSku;
}
