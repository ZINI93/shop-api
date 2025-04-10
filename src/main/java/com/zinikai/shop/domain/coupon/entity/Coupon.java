package com.zinikai.shop.domain.coupon.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
@Entity
public class Coupon extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long id;

    @Column(name = "coupon_uuid", updatable = false, nullable = false, unique = true)
    private String couponUuid;

    @Column(name = "owner_uuid", updatable = false, nullable = false)
    private String ownerUuid;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "min_order_Amount")
    private BigDecimal minOrderAmount;

    @Column(name = "discount_value")
    private BigDecimal discountValue;   // CASH, PERCENT

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(nullable = false)
    private String description;

    @Column(name = "max_usage")
    private Integer maxUsage;


    @Builder
    public Coupon(String couponUuid, String ownerUuid, String name, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minOrderAmount, BigDecimal discountValue, DiscountType discountType, String description, Integer maxUsage) {
        this.couponUuid = UUID.randomUUID().toString();
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderAmount = minOrderAmount;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.description = description;
        this.maxUsage = maxUsage;
    }


    public CouponResponseDto toResponse() {
        return CouponResponseDto.builder()
                .couponUuid(this.getCouponUuid())
                .name(this.name)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .minOrderAmount(this.minOrderAmount)
                .discountValue(this.discountValue)
                .discountType(this.discountType)
                .description(this.description)
                .maxUsage(this.maxUsage)
                .build();
    }

    public void updateCoupon(LocalDateTime startDate, LocalDateTime endDate, BigDecimal minOrderAmount, BigDecimal discountValue, DiscountType discountType, String description, Integer maxUsage) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderAmount = minOrderAmount;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.description = description;
        this.maxUsage = maxUsage;
    }
}
