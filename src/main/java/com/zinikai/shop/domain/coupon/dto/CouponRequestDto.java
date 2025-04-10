package com.zinikai.shop.domain.coupon.dto;

import com.zinikai.shop.domain.coupon.entity.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponRequestDto {

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderAmount;
    private BigDecimal discountValue;
    private DiscountType discountType;
    private String description;
    private Integer maxUsage;

}
