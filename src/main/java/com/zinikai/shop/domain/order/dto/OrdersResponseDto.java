package com.zinikai.shop.domain.order.dto;

import com.querydsl.codegen.ParameterizedTypeImpl;
import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersResponseDto {

    private Long id;
    private Long memberId;
    private BigDecimal totalAmount;
    private Status status;
    private String paymentMethod;

    @Builder @QueryProjection
    public OrdersResponseDto(Long id, Long memberId, BigDecimal totalAmount, Status status, String paymentMethod) {
        this.id = id;
        this.memberId = memberId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }


}
