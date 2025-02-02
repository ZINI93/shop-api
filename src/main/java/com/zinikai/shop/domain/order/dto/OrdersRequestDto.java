package com.zinikai.shop.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersRequestDto {

    private Long memberId;

    @NotNull
    @Min(value = 1 , message = "総量は１ー１０００で入力してください。")
    @Max(value = 10000 , message = "総量は１ー１０００で入力してください。")
    private BigDecimal totalAmount;

    @NotBlank(message = "注文のステータスが必要です。")
    private Status status;

    @NotBlank(message = "お支払いの方法が必要です。")
    private String paymentMethod;

    @Builder
    public OrdersRequestDto(Long member, BigDecimal totalAmount, Status status, String paymentMethod) {
        this.memberId = member;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
