package com.zinikai.shop.domain.order.repository;

import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrdersRepositoryCustom {

    // 주문 시간 순서, 멤버 - 주문검색 left join, 페이징,

    Page<OrdersResponseDto> searchOrders(String memberUuid,
                                         Status status,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         BigDecimal minAmount,
                                         BigDecimal maxAmount,
                                         String sortField,
                                         Pageable pageable);
}
