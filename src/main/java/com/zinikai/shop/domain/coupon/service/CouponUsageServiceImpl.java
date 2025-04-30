package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponUsageResponseDto;
import com.zinikai.shop.domain.coupon.entity.CouponUsage;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.repository.CouponUsageRepository;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.entity.Orders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponUsageServiceImpl implements CouponUsageService {

    private final CouponUsageRepository couponUsageRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CouponUsageResponseDto createCouponUsage(UserCoupon userCoupon, Orders order, BigDecimal discountAmount) {

            CouponUsage savedCouponUsage = CouponUsage.builder()
                    .userCoupon(userCoupon)
                    .orders(order)
                    .discountAmount(discountAmount)
                    .usedAt(userCoupon.getUsedAt())
                    .build();

            return couponUsageRepository.save(savedCouponUsage).toResponse();

    }


    @Override
    public CouponUsageResponseDto getCouponUsageInfo(String memberUuid, String couponUsageUuid) {
        CouponUsage couponUsage = couponUsageRepository.findByMemberUuidUsedAndCouponUsageUuid(memberUuid, couponUsageUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or couponUsage UUID"));

        return couponUsage.toResponse();


    }
}
