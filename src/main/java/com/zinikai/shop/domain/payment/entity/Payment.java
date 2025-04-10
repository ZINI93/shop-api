package com.zinikai.shop.domain.payment.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@Entity
public class Payment extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")  // 결제 방법을 늘어날 확장성으 고려해서  oneToone 을 사용하지 않는다.
    private Orders orders;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(name = "owner_uuid", nullable = false , updatable = false)
    private String ownerUuid;

    @Column(name = "payment_uuid", unique = true, nullable = false , updatable = false)
    private String paymentUuid;

    @Builder
    public Payment( Orders orders, PaymentStatus status, String paymentMethod,String ownerUuid, String paymentUuid) {
        this.orders = orders;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.ownerUuid = ownerUuid;
        this.paymentUuid = UUID.randomUUID().toString();
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

    public void paymentStatus(PaymentStatus status) {
        this.status = status;
    }
}
