package com.zinikai.shop.domain.category.repository;

import com.zinikai.shop.domain.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByProductProductUuid(String productUuid);
}