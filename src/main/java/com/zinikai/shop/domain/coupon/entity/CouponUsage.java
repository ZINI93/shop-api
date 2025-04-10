package com.zinikai.shop.domain.coupon.entity;


import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUsageResponseDto;
import com.zinikai.shop.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_usages")
@Entity
public class CouponUsage extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_usage_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", updatable = false, nullable = false)
    private UserCoupon userCoupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", updatable = false, nullable = false)
    private Orders orders;

    @Column(name = "coupon_usage_uuid")
    private String couponUsageUuid;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "member_uuid_used")
    private String memberUuidUsed;


    @Builder
    public CouponUsage(UserCoupon userCoupon, Orders orders, String couponUsageUuid, BigDecimal discountAmount, LocalDateTime usedAt) {
        this.userCoupon = userCoupon;
        this.orders = orders;
        this.couponUsageUuid = UUID.randomUUID().toString();
        this.discountAmount = discountAmount;
        this.usedAt = usedAt;
        this.memberUuidUsed = userCoupon.getMember().getMemberUuid();
    }


    public CouponUsageResponseDto toResponse() {
        return CouponUsageResponseDto.builder()
                .userCouponId(this.userCoupon.getId())
                .ordersId(this.orders.getId())
                .couponUsageUuid(this.couponUsageUuid)
                .discountAmount(this.discountAmount)
                .usedAt(this.getUsedAt())
                .build();
    }


}


