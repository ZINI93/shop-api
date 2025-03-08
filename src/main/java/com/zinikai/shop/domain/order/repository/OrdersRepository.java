package com.zinikai.shop.domain.order.repository;

import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.entity.Orders;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> , OrdersRepositoryCustom {

    Optional<Orders> findByMemberId(Long memberId);
    Optional<Orders> findByMemberMemberUuid(String memberUuid);
    Optional<Orders> findByMemberMemberUuidAndOrderUuid(String memberUuid, String orderUuid);
    Page<Orders> findAllByMemberMemberUuid(String memberUuid, Pageable pageable);
    Optional<Orders> findByOrderUuid(String orderUuid);
}