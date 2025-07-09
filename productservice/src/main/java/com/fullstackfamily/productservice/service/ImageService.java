package com.fullstackfamily.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fullstackfamily.productservice.dto.ApiResponse;
import com.fullstackfamily.productservice.entity.Images;
import com.fullstackfamily.productservice.entity.Product;
import com.fullstackfamily.productservice.repository.ImageRepository;
import com.fullstackfamily.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {
    private final Cloudinary cloudinary;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ResponseEntity<ApiResponse> saveImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (file.getSize() == 0 || originalFilename == null || !originalFilename.matches("(TSH|TRS|SWT|OUT)\\d+-\\d+\\.(png|jpg|jpeg)")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Не вірний формат файлу або файл не існує"));
            }
            String sku = originalFilename.substring(0, originalFilename.indexOf("-"));
            Optional<Product> product = productRepository.findBySku(sku);
            if (product.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Товар з SKU '" + sku + "' не знайдено"));
            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();
            Images image = new Images();
            image.setUrl(imageUrl);
            image.setProduct(product.get());
            imageRepository.save(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Фото збережено"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Помилка при завантаженні фото"));
        }
    }
}
