package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductImageUpdateDto {
    private String imageUrl;

    @Builder
    public ProductImageUpdateDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
