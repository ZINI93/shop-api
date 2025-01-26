package com.zinikai.shop.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersRequestDto {

    private Member member;
    private BigDecimal totalAmount;
    private Status status;
    private String paymentMethod;

    @Builder
    public OrdersRequestDto(Member member, BigDecimal totalAmount, Status status, String paymentMethod) {
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
