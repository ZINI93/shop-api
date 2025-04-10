package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
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

    @Override @Transactional
    public CouponResponseDto createCoupon(String memberUuid, CouponRequestDto requestDto) {

        log.info("Creating coupon for member UUID:{}", memberUuid);

        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID"));

        if (member.getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("Coupons cant be created except for admin");
        }

        Coupon coupon = Coupon.builder()
                .ownerUuid(member.getMemberUuid())
                .name(requestDto.getName())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .minOrderAmount(requestDto.getMinOrderAmount())
                .discountValue(requestDto.getDiscountValue())
                .discountType(requestDto.getDiscountType())
                .description(requestDto.getDescription())
                .maxUsage(requestDto.getMaxUsage())
                .build();

        if (coupon.getStartDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Coupon can only be issued after the current time");
        }

        if (coupon.getEndDate().isBefore(coupon.getStartDate())){
            throw new IllegalArgumentException("Set the coupon period expiration date to after the start time");
        }


        log.info("Created coupon: UUID={}, owner: UUID={}",
                coupon.getCouponUuid(), coupon.getOwnerUuid());


        return couponRepository.save(coupon).toResponse();
    }

    @Override
    public Page<CouponResponseDto> searchCoupon(LocalDateTime startDate, LocalDateTime endDate, String name, Pageable pageable) {

        Page<Coupon> coupons = couponRepository.findByCoupons(startDate, endDate, name, pageable);

        return coupons.map(Coupon::toResponse);
    }

    @Override
    public CouponResponseDto getCouponInfo(String memberUuid, String couponUuid) {

        log.info("Searching coupon for member UUID:{}", memberUuid);

        Coupon coupon = getCoupon(memberUuid, couponUuid);

        return coupon.toResponse();

    }

    @Override
    @Transactional
    public CouponResponseDto updateCoupon(String memberUuid, String couponUuid, CouponUpdateDto updateDto) {

        log.info("Updating order for member UUID:{}, coupon UUID:{}", memberUuid, couponUuid);


        Coupon coupon = getCoupon(memberUuid, couponUuid);


        if (coupon.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Already started coupon cannot be updated");
        }

        coupon.updateCoupon(updateDto.getStartDate(), updateDto.getEndDate(), updateDto.getMinOrderAmount(), updateDto.getDiscountValue(), updateDto.getDiscountType(),
                updateDto.getDescription(), updateDto.getMaxUsage());

        log.info("Updated coupon:{}", coupon);

        return coupon.toResponse();
    }

    @Override
    @Transactional
    public void deleteCoupon(String memberUuid, String couponUuid) {

        log.info("Deleting order for member UUID:{}, coupon UUID:{}", memberUuid, couponUuid);

        Coupon coupon = getCoupon(memberUuid, couponUuid);

        if (coupon.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Already started coupon cannot be updated");
        }

        couponRepository.delete(coupon);
    }

    private Coupon getCoupon(String memberUuid, String couponUuid) {
        return couponRepository.findByOwnerUuidAndCouponUuid(memberUuid, couponUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID: " + memberUuid + "coupon UUID:" + couponUuid));
    }
}

