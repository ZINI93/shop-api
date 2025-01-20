package com.zinikai.shop.domain.payment.entity;

import com.zinikai.shop.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orders_id")  // 결제 방법을 늘어날 확장성으 고려해서  oneToone 을 사용하지 않는다.
    private Orders orders;


    private String status;
    private String paymentMethod;
}
