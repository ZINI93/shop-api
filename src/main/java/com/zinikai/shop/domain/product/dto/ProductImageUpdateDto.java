package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductImageUpdateDto {

    @NotBlank(message = "イメージのURLを入力してください")
    private String imageUrl;

    @Builder
    public ProductImageUpdateDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
