package com.zinikai.shop.domain.order.dto;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersUpdateDto {

    @NotBlank(message = "注文のステータスが必要です。")
    private Status status;

    @Builder
    public OrdersUpdateDto(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.status = status;
    }
}
