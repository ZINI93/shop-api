package com.zinikai.shop.domain.category.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.category.dto.ProductCategoryResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_category")
@Entity
public class ProductCategory extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_category_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_category_uuid")
    private String productCategoryUuid;


    @Builder
    public ProductCategory(Category category, Product product, String productCategoryUuid) {
        this.category = category;
        this.product = product;
        this.productCategoryUuid = UUID.randomUUID().toString();
    }

    public ProductCategoryResponseDto toResponseDto(){

        return ProductCategoryResponseDto.builder()
                .categoryUuid(category.getCategoryUuid())
                .productUuid(product.getProductUuid())
                .build();

    }

    public void linkCategory(Category category, Product product) {
        this.category = category;
        this.product = product;
    }
}
