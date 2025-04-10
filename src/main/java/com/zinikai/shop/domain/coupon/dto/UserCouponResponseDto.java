package com.zinikai.shop.domain.coupon.dto;

import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.member.entity.Member;
import jakarta.persistence.*;import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCouponResponseDto {


    private String memberUuid;

    private String CouponUuid;

    private LocalDateTime usedAt;

    private Boolean isUsed  = false;

    private String userCouponUuid;


    @Builder
    public UserCouponResponseDto(String memberUuid, String couponUuid, LocalDateTime usedAt, Boolean isUsed, String userCouponUuid) {
        this.memberUuid = memberUuid;
        CouponUuid = couponUuid;
        this.usedAt = usedAt;
        this.isUsed = isUsed;
        this.userCouponUuid = userCouponUuid;
    }
}
