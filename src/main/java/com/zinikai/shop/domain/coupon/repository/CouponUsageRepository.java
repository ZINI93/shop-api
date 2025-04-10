package com.zinikai.shop.domain.coupon.repository;

import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.CouponUsage;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    Optional<CouponUsage> findByMemberUuidUsedAndCouponUsageUuid(String memberUuid, String couponUsageUuid);
}