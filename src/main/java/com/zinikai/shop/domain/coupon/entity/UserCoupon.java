package com.zinikai.shop.domain.coupon.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
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
@Table(name = "user_coupons")
@Entity
public class UserCoupon extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false, nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", updatable = false, nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders order;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "user_coupon_uuid", updatable = false, nullable = false, unique = true)
    private String userCouponUuid;


    @Builder
    public UserCoupon(Member member, Coupon coupon, Orders order, LocalDateTime usedAt, Boolean isUsed, String userCouponUuid) {
        this.member = member;
        this.coupon = coupon;
        this.order = order;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
        this.userCouponUuid = UUID.randomUUID().toString();
    }


    public UserCouponResponseDto toResponse() {

        return UserCouponResponseDto.builder()
                .memberUuid(this.member.getMemberUuid())
                .couponUuid(this.coupon.getCouponUuid())
                .usedAt(this.usedAt)
                .isUsed(this.isUsed)
                .userCouponUuid(this.userCouponUuid)
                .build();
    }

    public void usingCoupon(LocalDateTime usedAt, Orders order) {
        this.order = order;
        this.usedAt = usedAt;
        this.isUsed = true;
    }

    public void cancelCoupon() {
        this.order = null;
        this.usedAt = null;
        this.isUsed = false;
    }

    public BigDecimal applyDiscount(BigDecimal totalAmount, Coupon coupon) {

        if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new IllegalStateException("The coupon must exceed " + coupon.getMinOrderAmount() + "minimum amount");
        }

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return totalAmount.multiply(
                    BigDecimal.valueOf(1).subtract(
                            coupon.getDiscountValue().divide(BigDecimal.valueOf(100)))
            );
        } else {
            return totalAmount.subtract(coupon.getDiscountValue());
        }
    }

    // 全額から割引を計算
    public BigDecimal calculateDiscountAmount(BigDecimal totalAmount, Coupon coupon) {

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return totalAmount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
        }
        if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return coupon.getDiscountValue();
        }
        return BigDecimal.ZERO;
    }

}
