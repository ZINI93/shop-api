package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageRequestDto {

    private Product product;

    private String imageUrl;


    public ProductImageRequestDto(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
    }
}
