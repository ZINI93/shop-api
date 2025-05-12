package com.zinikai.shop.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersResponseDto {

    private BigDecimal totalAmount;
    private Status status;
    private String paymentMethod;
    private String sellerUuid;
    private String orderUuid;
    private List<ProductResponseDto> orderItems;
    private String userCouponUuid;


    @QueryProjection
    public OrdersResponseDto(BigDecimal totalAmount, Status status, String paymentMethod,String sellerUuid) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.sellerUuid = sellerUuid;
    }

    @Builder
    public OrdersResponseDto(BigDecimal totalAmount, Status status, String paymentMethod, String sellerUuid, String orderUuid, List<ProductResponseDto> orderItems,String userCouponUuid) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.sellerUuid = sellerUuid;
        this.orderUuid = orderUuid;
        this.orderItems = orderItems;
        this.userCouponUuid = userCouponUuid;
    }

    public OrdersResponseDto(List<ProductResponseDto> orderItems, String orderUuid, BigDecimal totalAmount) {
        this.orderItems = orderItems;
        this.orderUuid = orderUuid;
        this.totalAmount = totalAmount;
    }
}
