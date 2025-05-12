package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductImageUpdateDto {

    private String ImageUuid;

    @NotBlank(message = "イメージのURLを入力してください")
    private String imageUrl;

    @Builder
    public ProductImageUpdateDto(String imageUuid, String imageUrl) {
        ImageUuid = imageUuid;
        this.imageUrl = imageUrl;
    }
}
