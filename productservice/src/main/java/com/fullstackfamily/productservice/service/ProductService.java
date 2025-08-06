package com.fullstackfamily.productservice.service;

import com.fullstackfamily.productservice.dto.*;
import com.fullstackfamily.productservice.entity.*;
import com.fullstackfamily.productservice.repository.ProductRepository;
import com.fullstackfamily.productservice.repository.CategoryRepository;
import com.fullstackfamily.productservice.repository.BrandRepository;
import com.fullstackfamily.productservice.repository.ColorRepository;

import com.fullstackfamily.productservice.specification.ProductSpecification;
import com.fullstackfamily.productservice.validation.ValidationRequest;
import com.fullstackfamily.productservice.dto.APIResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;

    public ResponseEntity<List<ProductResponse>> getAllProducts(ProductFilterRequest filter, Optional<SortType> sort) {
        Specification<Product> spec = ProductSpecification.withFilter(filter);
        Sort sorting = Sort.unsorted();

        if (sort.isPresent()) {
            switch (sort.get()) {
                case PRICE_ASC -> sorting = Sort.by(Sort.Direction.ASC, "price");
                case PRICE_DESC -> sorting = Sort.by(Sort.Direction.DESC, "price");
                case NEW -> sorting = Sort.by("newCollection").descending();
                case POPULAR -> sorting = Sort.by("topSales").descending();
                case DISCOUNT -> sorting = Sort.by("hasdiscount").descending();
            }
        }

        List<Product> products = productRepository.findAll(spec, sorting);

        if (filter.getSize() != null) {
            products = products.stream()
                    .filter(p -> p.getSizes().entrySet().stream()
                            .anyMatch(e -> filter.getSize().contains(e.getKey()) && e.getValue() > 0))
                    .toList();
        }

        List<ProductResponse> productResponses = products.stream()
                .map(e -> new ProductResponse(
                        e.getSku(),
                        e.getName(),
                        e.getBrand().getName(),
                        e.getGender(),
                        e.getCategory().getName(),
                        e.getPrice(),
                        e.getOldPrice(),
                        e.getHasdiscount(),
                        e.getNewCollection(),
                        e.getTopSales(),
                        e.getImage().stream().map(Images::getUrl).toList(),
                        e.getSizes(),
                        e.getColor().getName(),
                        e.getSeason(),
                        e.getDescription(),
                        e.getMaterial()))
                .toList();
        return ResponseEntity.ok(productResponses);
    }
    public ResponseEntity<ProductResponse> getProductBySku(String sku) {
        Optional<Product> product = productRepository.findBySku(sku);
        return product.map(value ->
                ResponseEntity.ok(
                        new ProductResponse(
                                value.getSku(),
                                value.getName(),
                                value.getBrand().getName(),
                                value.getGender(),
                                value.getCategory().getName(),
                                value.getPrice(),
                                value.getOldPrice(),
                                value.getHasdiscount(),
                                value.getNewCollection(),
                                value.getTopSales(),
                                value.getImage().stream().map(Images::getUrl).toList(),
                                value.getSizes(),
                                value.getColor().getName(),
                                value.getSeason(),
                                value.getDescription(),
                                value.getMaterial())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    public ResponseEntity<APIResponse> createProduct(ProductInfoRequest request) {
        if (request.getSku() == null || request.getSku().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse("Sku є обов'язвовим поле для заповнення."));
        }
        Optional<Product> productBySku = productRepository.findBySku(request.getSku());
        if (productBySku.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse("Товар із цим sku вже є"));
        } else if (ValidationRequest.isNullOrEmpty(request.getName()) ||
                request.getBrandId() == null ||
                ValidationRequest.isNullOrEmpty(request.getGender()) ||
                request.getCategoryId() == null ||
                request.getPrice() == null ||
                request.getSizes() == null || request.getSizes().isEmpty() ||
                request.getColorId() == null ||
                ValidationRequest.isNullOrEmpty(request.getSeason()) ||
                ValidationRequest.isNullOrEmpty(request.getDescription()) ||
                ValidationRequest.isNullOrEmpty(request.getMaterial())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse("Всі обов’язкові поля мають бути заповнені"));
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid color ID"));

        if (request.getHasdiscount() &&
                request.getPrice().compareTo(request.getOldPrice()) >= 0) {
            throw new IllegalArgumentException("Discounted price must be less than old price.");
        }

            Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setBrand(brand);
        product.setGender(request.getGender());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setOldPrice(request.getOldPrice());
        product.setHasdiscount(request.getHasdiscount());
        product.setNewCollection(request.getNewCollection());
        product.setTopSales(request.getTopSales());
        product.setSizes(request.getSizes());
        product.setColor(color);
        product.setSeason(request.getSeason());
        product.setDescription(request.getDescription());
        product.setMaterial(request.getMaterial());
        productRepository.save(product);
        return ResponseEntity.ok(new APIResponse("Товар успішно створений"));
    }
    public ResponseEntity<APIResponse> updateProduct(String sku, UpdateProductRequest request) {
        Optional<Product> product = productRepository.findBySku(sku);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse("Товар не знайдено"));
        }

        if (request.getSku() != null) product.get().setSku(request.getSku());
        if (request.getName() != null) product.get().setName(request.getName());
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID"));
            product.get().setBrand(brand);
        }
        if (request.getGender() != null) product.get().setGender(request.getGender());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID"));
            product.get().setCategory(category);
        }
        if (request.getPrice() != null) product.get().setPrice(request.getPrice());
        if (request.getOldPrice() != null) product.get().setOldPrice(request.getOldPrice());
        if (request.getHasdiscount() != null) product.get().setHasdiscount(request.getHasdiscount());
        if (request.getNewCollection() != null) product.get().setNewCollection(request.getNewCollection());
        if (request.getTopSales() != null) product.get().setTopSales(request.getTopSales());
        if (request.getSizes() != null) product.get().setSizes(request.getSizes());
        if (request.getColorId() != null) {
            Color color = colorRepository.findById(request.getColorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid color ID"));
            product.get().setColor(color);
        }
        if (request.getSeason() != null) product.get().setSeason(request.getSeason());
        if (request.getDescription() != null) product.get().setDescription(request.getDescription());
        if (request.getMaterial() != null) product.get().setMaterial(request.getMaterial());

        productRepository.save(product.get());

        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse("Товар успішно оновлено"));
    }
    public ResponseEntity<APIResponse> deleteProduct(String sku) {
        Optional<Product> product = productRepository.findBySku(sku);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse("Не знайдено товар"));
        }
        productRepository.delete(product.get());
        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse("Товар видалено"));
    }
}
