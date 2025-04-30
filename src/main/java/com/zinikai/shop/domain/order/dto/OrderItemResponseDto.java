package com.zinikai.shop.domain.order.dto;

import com.zinikai.shop.domain.product.entity.Product;
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
    private Product product;


    @Builder
    public OrderItemResponseDto(Long orderItemId, Long orderId, Long productId, Integer quantity, BigDecimal price,String sellerUuid,Product product) {
        this.orderItemId = orderItemId;
        OrderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.sellerUuid = sellerUuid;
        this.product = product;
    }
}
