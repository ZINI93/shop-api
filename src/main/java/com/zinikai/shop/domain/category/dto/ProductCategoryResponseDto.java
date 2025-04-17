package com.zinikai.shop.domain.category.dto;


import lombok.Builder;
import lombok.Data;

@Data
public class ProductCategoryResponseDto {


    private String categoryUuid;
    private String productUuid;

    @Builder
    public ProductCategoryResponseDto(String categoryUuid, String productUuid) {
        this.categoryUuid = categoryUuid;
        this.productUuid = productUuid;
    }
}
