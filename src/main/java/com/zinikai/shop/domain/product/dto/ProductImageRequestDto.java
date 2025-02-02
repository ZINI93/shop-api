package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageRequestDto {

    @NotNull
    private Long productId;

    @NotBlank(message = "イメージのURLを入力してください")
    private String imageUrl;


    public ProductImageRequestDto(Long productId, String imageUrl) {
        this.productId = productId;
        this.imageUrl = imageUrl;
    }
}
