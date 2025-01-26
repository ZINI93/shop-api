package com.zinikai.shop.domain.cart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartUpdateDto {

    private Integer quantity;

    @Builder
    public CartUpdateDto(Integer quantity) {
        this.quantity = quantity;
    }
}
