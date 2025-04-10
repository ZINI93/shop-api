package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponUsageResponseDto;
import com.zinikai.shop.domain.coupon.entity.CouponUsage;
import com.zinikai.shop.domain.coupon.repository.CouponUsageRepository;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponUsageServiceImpl implements CouponUsageService{

    private final CouponUsageRepository couponUsageRepository;
    private final MemberRepository memberRepository;

    @Override
    public CouponUsageResponseDto getCouponUsageInfo(String memberUuid, String couponUsageUuid) {
        CouponUsage couponUsage = couponUsageRepository.findByMemberUuidUsedAndCouponUsageUuid(memberUuid, couponUsageUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or couponUsage UUID"));

        return couponUsage.toResponse();


    }
}
