package com.zinikai.shop.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrdersRequestDto {

    @NotBlank(message = "お支払いの方法が必要です。")
    private String paymentMethod;

    private String userCouponUuid;

    private List<OrderItemRequestDto> orderItems;


    @Builder
    public OrdersRequestDto(String paymentMethod, String userCouponUuid) {
        this.paymentMethod = paymentMethod;
        this.userCouponUuid = userCouponUuid;
    }
}
