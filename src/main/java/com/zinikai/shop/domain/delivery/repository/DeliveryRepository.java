package com.zinikai.shop.domain.delivery.repository;

import com.zinikai.shop.domain.delivery.entity.Delivery;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("select d from Delivery d left join fetch d.member m where (m.memberUuid = :memberUuid or d.buyerUuid = :memberUuid) and d.deliveryUuid = :deliverUuid")
    Optional<Delivery> findByMemberUuidAndDeliveryUuid(@Param("memberUuid") String memberUuid, @Param("deliverUuid") String deliveryUuid);

    @Query("SELECT d FROM Delivery d WHERE d.deliveryStatus = :status AND d.confirmDelivery IS NULL AND d.updatedAt < :updatedAt")
    List<Delivery> findOldDeliveries(@Param("status")DeliveryStatus status, @Param("updatedAt") LocalDateTime updateAt);

    Optional<Delivery> findByMemberMemberUuidAndDeliveryUuid(String memberUuid, String deliveryUuid);
    Optional<Delivery> findByBuyerUuidAndDeliveryUuid(String BuyerUuid, String deliveryUuid);

}