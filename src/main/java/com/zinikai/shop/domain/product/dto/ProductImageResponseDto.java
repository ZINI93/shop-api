package com.zinikai.shop.domain.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponseDto {

    private String imageUrl;
    private String productImageUuid;


    @Builder
    public ProductImageResponseDto(String imageUrl, String productImageUuid) {
        this.imageUrl = imageUrl;
        this.productImageUuid = productImageUuid;
    }
}
