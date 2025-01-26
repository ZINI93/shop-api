package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrdersService {

    OrdersResponseDto createOrder(OrdersRequestDto requestDto);
    OrdersResponseDto getOrderById(Long orderId);

    Page<OrdersResponseDto> searchOrder(Status status, LocalDateTime starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable);
    OrdersResponseDto updateOrder(Long orderId, OrdersUpdateDto updateDto);
    void  deleteOrder(Long orderId);
}
