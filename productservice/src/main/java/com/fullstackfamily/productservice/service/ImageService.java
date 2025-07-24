package com.fullstackfamily.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fullstackfamily.productservice.dto.APIResponse;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class ImageService {
    private final Cloudinary cloudinary;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ResponseEntity<APIResponse> saveImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (file.getSize() == 0 || originalFilename == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse("Не вірний формат файлу або файл не існує"));
            }

            Pattern pattern = Pattern.compile("((TSH|TRS|SWT|OUT)\\d+)-\\d+\\.(png|jpg|jpeg)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalFilename);
            if (!matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse("Невірний формат імені файлу"));
            }

            String sku = matcher.group(1);
            String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

            Optional<Product> product = productRepository.findBySku(sku);
            if (product.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIResponse("Товар з SKU '" + sku + "' не знайдено"));
            } else if (imageRepository.existsByOriginalName(baseName)){
                return ResponseEntity.badRequest()
                        .body(new APIResponse("Таке фото вже існує"));
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();
            if (imageUrl == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new APIResponse("Не вдалося отримати URL завантаженого зображення"));
            }

            Images image = new Images();
            image.setOriginalName(baseName);
            image.setUrl(imageUrl);
            image.setProduct(product.get());
            imageRepository.save(image);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new APIResponse("Фото збережено"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Помилка при завантаженні фото"));
        }
    }
}
