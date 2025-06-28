package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {
    @Value("${local.name.directory}")
    private String RESOURCE_LOCATION;

    public ResponseEntity<ApiResponse> saveImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (file.getSize() == 0 || originalFilename == null || !originalFilename.matches("TSH\\d+-\\d+\\.(png|jpg|jpeg)")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Не вірний формат файлу або файл не існує"));
            }
            Path uploadDir = Paths.get(RESOURCE_LOCATION.replace("file:", ""));
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(originalFilename);
            Files.write(filePath, file.getBytes());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("/images/" + originalFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
