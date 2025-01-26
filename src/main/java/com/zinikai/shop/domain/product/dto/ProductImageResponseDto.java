package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponseDto {

    private Long id;

    private Product product;

    private String imageUrl;

    @Builder
    public ProductImageResponseDto(Long id, Product product, String imageUrl) {
        this.id = id;
        this.product = product;
        this.imageUrl = imageUrl;
    }
}
