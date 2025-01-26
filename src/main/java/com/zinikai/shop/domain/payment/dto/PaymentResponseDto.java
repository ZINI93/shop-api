package com.zinikai.shop.domain.payment.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Profile;

@Data
@Builder
public class PaymentResponseDto {

    private Long id;
    private Long orderId;
    private PaymentStatus status;
    private String paymentMethod;



    @Builder @QueryProjection
    public PaymentResponseDto(Long id, Long orderId, PaymentStatus status, String paymentMethod) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
