package com.fullstackfamily.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "images")
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oldUrl;
    private String newUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku")
    private Product product;
}
