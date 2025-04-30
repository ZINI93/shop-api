package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {

    OrderItem createAndSaveOrderItem(Member member, OrderItemRequestDto itemDto, Orders order, Product product);
    Page<OrderItemResponseDto> getOrderItems (String ownerUuid, Pageable pageable);

    OrderItemResponseDto getOrderItem(String memberUuid, String orderItemUuid);
    Page<OrderItemResponseDto> getSalesHistory(String ownerUuid, Pageable pageable);

    void refundStockByOrder(Orders order);
    void decreaseStockByOrderItem(OrdersRequestDto requestDto);
}
