package com.zinikai.shop.domain.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartUpdateDto {

    @NotNull
    @Min(value = 1, message = "数量は1から1000まで入力してください。" )
    @Max(value = 1000 , message = "数量は1から1000まで入力してください。")
    private Integer quantity;

    @Builder
    public CartUpdateDto(Integer quantity) {
        this.quantity = quantity;
    }
}
