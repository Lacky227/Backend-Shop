package com.fullstackfamily.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fullstackfamily.productservice.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageService {
    private final Cloudinary cloudinary;

    public ResponseEntity<ApiResponse> saveImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (file.getSize() == 0 || originalFilename == null || !originalFilename.matches("(TSH|TRS|SWT|OUT)\\d+-\\d+\\.(png|jpg|jpeg)")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Не вірний формат файлу або файл не існує"));
            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(imageUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
