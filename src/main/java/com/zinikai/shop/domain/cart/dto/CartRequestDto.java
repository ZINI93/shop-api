package com.zinikai.shop.domain.cart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartRequestDto {

    private Long memberId ;
    private Long productId;
    private Integer quantity;


    @Builder
    public CartRequestDto(Long memberId, Long productId, Integer quantity) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
