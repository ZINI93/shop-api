package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdersService {

    Orders createOrder(Member member, BigDecimal finalAmount, OrdersRequestDto requestDto, String sellerUuid, Address address, BigDecimal discountAmount);

    OrdersResponseDto orderProcess(String memberUuid, OrdersRequestDto requestDto);

    OrdersResponseDto orderProcessFromCart(String memberUuid, OrdersRequestDto requestDto);

    OrdersResponseDto getOrder(String memberUuid, String ordersUuid);

    Page<OrdersResponseDto> searchOrder(String memberUuid, Status status, LocalDateTime starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable);

    OrdersResponseDto cancelOrder(String memberUuid, String orderUuid);

    void deleteOrder(String memberUuid, String orderUuid);

    void autoCancelPendingOrders();
}
