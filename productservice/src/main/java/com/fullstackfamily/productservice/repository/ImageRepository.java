package com.fullstackfamily.productservice.repository;

import com.fullstackfamily.productservice.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Images, Long> {
    boolean existsByOriginalName(String originalName);
}
