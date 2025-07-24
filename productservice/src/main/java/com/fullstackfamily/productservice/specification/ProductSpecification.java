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
                predicates.add(root.get("category").in(filter.getCategory()));
            if (filter.getPrice_from() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getPrice_from()));
            if (filter.getPrice_to() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getPrice_to()));
            if (filter.getGender() != null)
                predicates.add(root.get("gender").in(filter.getGender()));
            if (filter.getColor() != null)
                predicates.add(root.get("color").in(filter.getColor()));
            if (filter.getSeason() != null)
                predicates.add(root.get("season").in(filter.getSeason()));
            if (filter.getBrand() != null)
                predicates.add(root.get("brand").in(filter.getBrand()));
            if (filter.getMaterial() != null)
                predicates.add(root.get("material").in(filter.getMaterial()));

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
