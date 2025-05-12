package com.zinikai.shop.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.product.entity.ProductCondition;
import com.zinikai.shop.domain.product.entity.ProductImage;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponseDto {

    private String name;
    private BigDecimal price;
    private String description;
    private Integer stock;
    private ProductCondition productCondition;
    private String productMaker;
    private String productUuid;
    private List<String> productImageUrls;

    @QueryProjection
    public ProductResponseDto(String name, BigDecimal price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    @Builder
    public ProductResponseDto(String name, BigDecimal price, String description, Integer stock, ProductCondition productCondition, String productMaker, String productUuid, List<String> productImageUrls) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productCondition = productCondition;
        this.productMaker = productMaker;
        this.productUuid = productUuid;
        this.productImageUrls = productImageUrls;
    }

    public ProductResponseDto(String name, BigDecimal price, String description, Integer stock, ProductCondition productCondition, String productMaker, String productUuid) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productCondition = productCondition;
        this.productMaker = productMaker;
        this.productUuid = productUuid;
    }
}
