package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
import com.zinikai.shop.domain.coupon.repository.UserCouponRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCouponServiceImpl implements UserCouponService {


    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;


    @Override
    @Transactional
    public UserCouponResponseDto couponIssuance(String memberUuid, String couponUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found Member UUID"));

        Coupon coupon = couponRepository.findByCouponUuid(couponUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found coupon UUID"));

        if (coupon.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Can only be issued if the coupon is valid");
        }

        boolean alreadyIssued = userCouponRepository.existsByMemberAndCoupon(member, coupon);
        if (alreadyIssued) {
            throw new IllegalStateException("This coupon has already been issued");
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .member(member)
                .coupon(coupon)
                .usedAt(null)
                .isUsed(false)
                .build();

        return userCouponRepository.save(userCoupon).toResponse();
    }

    @Override
    public UserCouponResponseDto getCouponInfo(String memberUuid, String userCouponUuid) {

        UserCoupon userCoupon = userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(memberUuid, userCouponUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID: " + memberUuid + "userCoupon UUID: " + userCouponUuid));


        return userCoupon.toResponse();
    }

    @Override
    public Page<UserCouponResponseDto> myCouponList(String memberUuid, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Page<UserCoupon> memberCoupons = userCouponRepository.findValidUserCoupons(memberUuid, now, pageable);

        return memberCoupons.map(UserCoupon::toResponse);
    }


    @Override
    @Transactional
    public void deleteCoupon(String memberUuid, String userCouponUuid) {
        UserCoupon userCoupon = userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(memberUuid, userCouponUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID: " + memberUuid + "userCoupon UUID: " + userCouponUuid));

        if (!userCoupon.getIsUsed().equals(false)) {
            throw new IllegalStateException("You cant delete used coupon");
        }
        userCouponRepository.delete(userCoupon);
    }
}
