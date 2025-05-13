package com.zinikai.shop.domain.coupon.service;


import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCouponService {

    UserCoupon createCoupon(Member member, Coupon coupon);

    UserCouponResponseDto couponIssuance(String memberUuid, String couponUuid);

    UserCouponResponseDto getCouponInfo(String memberUuid, String userCouponUuid);

    Page<UserCouponResponseDto> myAllCouponList(String memberUuid, Pageable pageable);

    Page<UserCouponResponseDto> myUnusedCouponList(String memberUuid, Pageable pageable);

    Page<UserCouponResponseDto> myUsedCouponList(String memberUuid, Pageable pageable);

    void deleteCoupon(String memberUuid, String userCouponUuid);


}
