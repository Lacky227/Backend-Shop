package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.APIResponse;
import com.fullstackfamily.productservice.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
@Tag(name = "Завантаження зображення", description = "REST-контролер для роботи із завантаженням зображень")
public class ImageController {
    private final ImageService imageService;

    @Operation(
            summary = "Завантаження зображення",
            description = "Приймає файл зображення і зберігає його на сервері"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Фото збережено",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Некоректний файл або ім'я файлу, або фото вже існує",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Товар не знайдено",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Внутрішня помилка сервера",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @PostMapping("/save-image")
    public ResponseEntity<APIResponse> saveImage(@RequestParam("file") MultipartFile file) {
        return imageService.saveImage(file);
    }
}
