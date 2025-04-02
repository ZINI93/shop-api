package com.zinikai.shop.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.order.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersResponseDto {

    private BigDecimal totalAmount;
    private Status status;
    private String paymentMethod;
    private String sellerUuid;
    private String orderUuid;

    @QueryProjection
    public OrdersResponseDto(BigDecimal totalAmount, Status status, String paymentMethod,String sellerUuid) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.sellerUuid = sellerUuid;
    }

    @Builder
    public OrdersResponseDto(BigDecimal totalAmount, Status status, String paymentMethod, String sellerUuid, String orderUuid) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.sellerUuid = sellerUuid;
        this.orderUuid = orderUuid;
    }
}
