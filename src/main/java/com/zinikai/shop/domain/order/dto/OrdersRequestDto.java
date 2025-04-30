package com.zinikai.shop.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class OrdersRequestDto {

    @NotBlank(message = "お支払いの方法が必要です。")
    private String paymentMethod;

    private String userCouponUuid;

    private List<OrderItemRequestDto> orderItems;


    @Builder
    public OrdersRequestDto(String paymentMethod, String userCouponUuid, List<OrderItemRequestDto> orderItems) {
        this.paymentMethod = paymentMethod;
        this.userCouponUuid = userCouponUuid;
        this.orderItems = orderItems;
    }
}
