package com.zinikai.shop.domain.coupon.dto;

import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponUsageResponseDto {

    private Long userCouponId;
    private Long ordersId;
    private String couponUsageUuid;
    private BigDecimal discountAmount;
    private LocalDateTime usedAt;



    @Builder
    public CouponUsageResponseDto(Long userCouponId, Long ordersId, String couponUsageUuid, BigDecimal discountAmount, LocalDateTime usedAt) {
        this.userCouponId = userCouponId;
        this.ordersId = ordersId;
        this.couponUsageUuid = couponUsageUuid;
        this.discountAmount = discountAmount;
        this.usedAt = usedAt;
    }
}
