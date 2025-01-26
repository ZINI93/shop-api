package com.zinikai.shop.domain.order.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders extends TimeStamp { // order은 mysql 예약어 이기 때문에 table을 생성하지 못한다;;;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String paymentMethod;

    @Builder
    public Orders(Long id, Member member , BigDecimal totalAmount, Status status, String paymentMethod) {
        this.id = id;
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public OrdersResponseDto toResponseDto(){

        return OrdersResponseDto.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .totalAmount(this.totalAmount)
                .status(this.status)
                .paymentMethod(this.paymentMethod)
                .build();
    }


    public void UpdateInfo(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
