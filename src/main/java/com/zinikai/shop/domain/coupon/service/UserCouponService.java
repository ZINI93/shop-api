package com.zinikai.shop.domain.coupon.service;


import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCouponService {

    UserCouponResponseDto couponIssuance(String memberUuid, String couponUuid);

    UserCouponResponseDto getCouponInfo(String memberUuid, String userCouponUuid);

    Page<UserCouponResponseDto> myCouponList(String memberUuid, Pageable pageable);

    void deleteCoupon(String memberUuid, String userCouponUuid);


}
