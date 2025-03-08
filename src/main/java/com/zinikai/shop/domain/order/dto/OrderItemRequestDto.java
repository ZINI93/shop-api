package com.zinikai.shop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
public class OrderItemRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private int quantity;


    @Builder
    public OrderItemRequestDto(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
