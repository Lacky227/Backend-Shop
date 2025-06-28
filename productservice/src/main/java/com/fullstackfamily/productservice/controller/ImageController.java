package com.fullstackfamily.productservice.controller;

import com.fullstackfamily.productservice.dto.ApiResponse;
import com.fullstackfamily.productservice.service.ImageService;
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
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/save-image")
    public ResponseEntity<ApiResponse> saveImage(@RequestParam("file") MultipartFile file) {
        return imageService.saveImage(file);
    }
}
