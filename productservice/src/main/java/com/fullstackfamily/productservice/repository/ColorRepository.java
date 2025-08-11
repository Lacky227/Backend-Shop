package com.fullstackfamily.productservice.repository;

import com.fullstackfamily.productservice.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Long> {
    boolean existsByNameIgnoreCase(String name);
}