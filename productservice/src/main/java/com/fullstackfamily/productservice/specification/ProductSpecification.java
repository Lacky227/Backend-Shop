package com.fullstackfamily.productservice.specification;

import com.fullstackfamily.productservice.dto.ProductFilterRequest;
import com.fullstackfamily.productservice.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> withFilter(ProductFilterRequest filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategory() != null)
                predicates.add(cb.lower(root.get("category"))
                        .in(filter.getCategory().stream()
                                .map(String::toLowerCase)
                                .toList()));
            if (filter.getPrice_from() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getPrice_from()));
            if (filter.getPrice_to() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getPrice_to()));
            if (filter.getGender() != null)
                predicates.add(cb.lower(root.get("gender"))
                        .in(filter.getGender().stream()
                                .map(String::toLowerCase)
                                .toList()));
            if (filter.getColor() != null)
                predicates.add(cb.lower(root.get("color"))
                        .in(filter.getColor().stream()
                                .map(String::toLowerCase)
                                .toList()));
            if (filter.getSeason() != null)
                predicates.add(cb.lower(root.get("season"))
                        .in(filter.getSeason().stream()
                                .map(String::toLowerCase)
                                .toList()));
            if (filter.getBrand() != null)
                predicates.add(cb.lower(root.get("brand"))
                        .in(filter.getBrand().stream()
                                .map(String::toLowerCase)
                                .toList()));
            if (filter.getMaterial() != null)
                predicates.add(cb.lower(root.get("material"))
                        .in(filter.getMaterial().stream()
                                .map(String::toLowerCase)
                                .toList()));

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
