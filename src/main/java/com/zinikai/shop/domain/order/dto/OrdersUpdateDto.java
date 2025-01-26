package com.zinikai.shop.domain.order.dto;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersUpdateDto {

    private BigDecimal totalAmount;
    private Status status;
    private String paymentMethod;


    @Builder
    public OrdersUpdateDto(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
