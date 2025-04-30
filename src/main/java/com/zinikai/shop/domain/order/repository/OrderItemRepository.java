package com.zinikai.shop.domain.order.repository;

import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = {"product"})
    List<OrderItem> findByOrders(Orders orders);
    Page<OrderItem> findAllByOwnerUuidOrderByCreatedAtDesc(String ownerUuid, Pageable pageable);
    Page<OrderItem> findByProductMemberMemberUuid(String memberUuid, Pageable pageable);
    Optional<OrderItem> findByOwnerUuidAndOrderItemUuid(String ownerUuid,String orderItemUuid);



}