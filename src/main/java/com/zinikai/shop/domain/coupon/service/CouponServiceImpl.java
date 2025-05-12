package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.exception.CouponNotFoundException;
import com.zinikai.shop.domain.coupon.exception.ValidateCouponException;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
import com.zinikai.shop.domain.member.exception.MemberRoleNotMatchException;
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
public class CouponServiceImpl implements CouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;

    @Override
    public Coupon createCoupon(Member member, CouponRequestDto requestDto) {

        return Coupon.builder()
                .member(member)
                .name(requestDto.getName())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .minOrderAmount(requestDto.getMinOrderAmount())
                .discountValue(requestDto.getDiscountValue())
                .discountType(requestDto.getDiscountType())
                .description(requestDto.getDescription())
                .maxUsage(requestDto.getMaxUsage())
                .build();

    }

    @Override
    public CouponResponseDto createCouponWithValidate(String memberUuid, CouponRequestDto requestDto) {

        log.info("Creating coupon for member UUID:{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);
        checkMemberRole(member);

        Coupon coupon = createCoupon(member, requestDto);
        validateCouponStartDate(coupon);
        validateCouponEndDate(coupon);
        Coupon savedCoupon = couponRepository.save(coupon);

        log.info("Created coupon: UUID={}", coupon.getCouponUuid());

        return savedCoupon.toResponse();
    }

    @Override
    public Page<CouponResponseDto> searchCoupon(LocalDateTime startDate, LocalDateTime endDate, String name, Pageable pageable) {

        Page<Coupon> coupons = couponRepository.findByCoupons(startDate, endDate, name, pageable);

        return coupons.map(Coupon::toResponse);
    }

    @Override
    public CouponResponseDto getCouponInfo(String memberUuid, String couponUuid) {

        log.info("Searching coupon for member UUID:{}", memberUuid);

        Coupon coupon = findMemberByMemberUuidAndCouponUuid(memberUuid, couponUuid);

        return coupon.toResponse();

    }

    @Override
    @Transactional
    public CouponResponseDto updateCoupon(String memberUuid, String couponUuid, CouponUpdateDto updateDto) {

        log.info("Updating order for member UUID:{}, coupon UUID:{}", memberUuid, couponUuid);

        Coupon coupon = findMemberByMemberUuidAndCouponUuid(memberUuid, couponUuid);
        validateCouponStartDate(coupon);
        validateCouponEndDate(coupon);

        coupon.updateCoupon(
                updateDto.getStartDate(),
                updateDto.getEndDate(),
                updateDto.getMinOrderAmount(),
                updateDto.getDiscountValue(),
                updateDto.getDiscountType(),
                updateDto.getDescription(),
                updateDto.getMaxUsage());

        log.info("Updated coupon UUID:{}", coupon.getCouponUuid());

        return coupon.toResponse();
    }

    @Override
    @Transactional
    public void deleteCoupon(String memberUuid, String couponUuid) {

        log.info("Deleting coupon for member UUID:{}, coupon UUID:{}", memberUuid, couponUuid);

        Coupon coupon = findMemberByMemberUuidAndCouponUuid(memberUuid, couponUuid);
        validateCouponStartDate(coupon);

        couponRepository.delete(coupon);
    }

    private Coupon findMemberByMemberUuidAndCouponUuid(String memberUuid, String couponUuid) {
        return couponRepository.findByMemberMemberUuidAndCouponUuid(memberUuid, couponUuid)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found: member UUID:" + memberUuid +"coupon UUID:" + couponUuid));
    }

    private void checkMemberRole(Member member) {
        if (member.getRole() != MemberRole.ADMIN) {
            throw new MemberRoleNotMatchException("Only admin users can create coupon");
        }
    }


    private void validateCouponStartDate(Coupon coupon) {
        if (coupon.getStartDate().isBefore(LocalDateTime.now())){
            throw new ValidateCouponException("Coupon can only be issued after the current time");
        }
    }

    private void validateCouponEndDate(Coupon coupon){
        if (coupon.getEndDate().isBefore(coupon.getStartDate())){
            throw new ValidateCouponException("Set the coupon period expiration date to after the start time");
        }
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }
}

