package com.zinikai.shop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
public class OrderItemRequestDto {

    @NotNull
    private String productUuid;

    @NotNull
    private int quantity;


    @Builder
    public OrderItemRequestDto(String productUuid, int quantity) {
        this.productUuid = productUuid;
        this.quantity = quantity;
    }
}
