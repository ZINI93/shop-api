package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.exception.CouponNotFoundException;
import com.zinikai.shop.domain.coupon.exception.UserCouponNotFoundException;
import com.zinikai.shop.domain.coupon.exception.ValidateCouponException;
import com.zinikai.shop.domain.coupon.exception.ValidateUserCouponException;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
import com.zinikai.shop.domain.coupon.repository.UserCouponRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
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
    public UserCoupon createCoupon(Member member, Coupon coupon) {

        return UserCoupon.builder()
                .member(member)
                .coupon(coupon)
                .usedAt(null)
                .isUsed(false)
                .build();
    }

    @Override @Transactional
    public UserCouponResponseDto couponIssuance(String memberUuid, String couponUuid) {

        log.info("Creating userCoupon for MemberUuid:{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);
        Coupon coupon = findCouponByCouponUuid(couponUuid);

        validateStartDate(coupon);
        validateCouponIssued(member, coupon);

        UserCoupon userCoupon = createCoupon(member, coupon);
        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);

        log.info("Created userCoupon UUID:{}", savedUserCoupon.getUserCouponUuid());

        return savedUserCoupon.toResponse();
    }

    @Override
    public UserCouponResponseDto getCouponInfo(String memberUuid, String userCouponUuid) {

        UserCoupon userCoupon = findUserCouponByMemberUuidAndCouponUuid(memberUuid, userCouponUuid);

        return userCoupon.toResponse();
    }


    @Override
    public Page<UserCouponResponseDto> myAllCouponList(String memberUuid, Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        Page<UserCoupon> memberCoupons = userCouponRepository.findAllUserCoupons(memberUuid, now, pageable);

        return memberCoupons.map(UserCoupon::toResponse);
    }

    @Override
    public Page<UserCouponResponseDto> myUnusedCouponList(String memberUuid, Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        Page<UserCoupon> unusedCoupons = userCouponRepository.findUsableCoupons(memberUuid, now, pageable);

        return unusedCoupons.map(UserCoupon::toResponse);
    }

    @Override
    public Page<UserCouponResponseDto> myUsedCouponList(String memberUuid, Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        Page<UserCoupon> usedCoupons = userCouponRepository.findUsedCoupons(memberUuid, now, pageable);

        return usedCoupons.map(UserCoupon::toResponse);
    }


    @Override
    @Transactional
    public void deleteCoupon(String memberUuid, String userCouponUuid) {

        log.info("Deleting userCoupon for member UUID:{}", memberUuid);

        UserCoupon userCoupon = findUserCouponByMemberUuidAndCouponUuid(memberUuid, userCouponUuid);

        validateUserCouponIssued(userCoupon);
        userCouponRepository.delete(userCoupon);
    }

    private void validateUserCouponIssued(UserCoupon userCoupon) {
        if (Boolean.TRUE.equals(userCoupon.getIsUsed())) {
            throw new ValidateUserCouponException("Only unused UserCoupon can be deleted");
        }
    }

    private void validateCouponIssued(Member member, Coupon coupon) {
        boolean alreadyIssued = userCouponRepository.existsByMemberAndCoupon(member, coupon);
        if (alreadyIssued) {
            throw new ValidateCouponException("This coupon has already been issued");
        }
    }


    private void validateStartDate(Coupon coupon) {
        if (coupon.getStartDate().isAfter(LocalDateTime.now())) {
            throw new ValidateCouponException("Can only be issued if the coupon is valid");
        }
    }

    private Coupon findCouponByCouponUuid(String couponUuid) {
        return couponRepository.findByCouponUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException("Coupon Not found"));
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Member Not found"));
    }

    private UserCoupon findUserCouponByMemberUuidAndCouponUuid(String memberUuid, String userCouponUuid) {
        return userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(memberUuid, userCouponUuid)
                .orElseThrow(() -> new UserCouponNotFoundException("UserCoupon not found for member UUID: " + memberUuid + "userCoupon UUID: " + userCouponUuid));
    }
}
