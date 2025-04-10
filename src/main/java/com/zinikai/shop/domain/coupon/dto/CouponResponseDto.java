package com.zinikai.shop.domain.coupon.dto;

import com.zinikai.shop.domain.coupon.entity.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
public class CouponResponseDto {

    private String couponUuid;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderAmount;
    private BigDecimal discountValue;
    private DiscountType discountType;
    private String description;
    private Integer maxUsage;


    @Builder
    public CouponResponseDto(String couponUuid, String name, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minOrderAmount, BigDecimal discountValue, DiscountType discountType, String description, Integer maxUsage) {
        this.couponUuid = couponUuid;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderAmount = minOrderAmount;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.description = description;
        this.maxUsage = maxUsage;

    }
}
