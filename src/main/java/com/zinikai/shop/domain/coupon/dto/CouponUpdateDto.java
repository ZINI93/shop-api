package com.zinikai.shop.domain.coupon.dto;

import com.zinikai.shop.domain.coupon.entity.DiscountType;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponUpdateDto {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderAmount;
    private BigDecimal discountValue;
    private DiscountType discountType;
    private String description;
    private Integer maxUsage;

    public CouponUpdateDto(LocalDateTime startDate, LocalDateTime endDate, BigDecimal minOrderAmount, BigDecimal discountValue, DiscountType discountType, String description, Integer maxUsage) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderAmount = minOrderAmount;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.description = description;
        this.maxUsage = maxUsage;
    }
}
