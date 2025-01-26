package com.zinikai.shop.domain.payment.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orders_id")  // 결제 방법을 늘어날 확장성으 고려해서  oneToone 을 사용하지 않는다.
    private Orders orders;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Column(nullable = false)
    private String paymentMethod;


    @Builder
    public Payment(Long id, Orders orders, PaymentStatus status, String paymentMethod) {
        this.id = id;
        this.orders = orders;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public PaymentResponseDto toResponse(){
        return PaymentResponseDto.builder()
                .id(this.id)
                .orderId(this.orders.getId())
                .status(this.status)
                .paymentMethod(this.paymentMethod)
                .build();
    }

    public void updateInfo(PaymentStatus status, String paymentMethod){
        this.status = status;
        this.paymentMethod = paymentMethod;

    }
}
