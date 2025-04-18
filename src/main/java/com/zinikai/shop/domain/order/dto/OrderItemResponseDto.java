package com.zinikai.shop.domain.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDto {

    private Long orderItemId;
    private Long OrderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private String sellerUuid;


    @Builder
    public OrderItemResponseDto(Long orderItemId, Long orderId, Long productId, Integer quantity, BigDecimal price,String sellerUuid) {
        this.orderItemId = orderItemId;
        OrderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.sellerUuid = sellerUuid;
    }
}
