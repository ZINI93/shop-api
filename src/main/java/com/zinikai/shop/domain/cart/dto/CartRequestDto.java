package com.zinikai.shop.domain.cart.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartRequestDto {

    @NotNull
    private Long memberId ;

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "数量は1から1000まで入力してください。" )
    @Max(value = 1000 , message = "数量は1から1000まで入力してください。")
    private final Integer quantity;

    @Builder
    public CartRequestDto(Long memberId, Long productId, Integer quantity) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
