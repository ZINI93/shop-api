package com.zinikai.shop.domain.order.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "order_uuid", nullable = false, updatable = false, unique = true)
    private String orderUuid;

    @Column(name = "seller_uuid", nullable = false, updatable = false)
    private String sellerUuid;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Builder
    public Orders(Member member, BigDecimal totalAmount, Status status, String paymentMethod, String orderUuid, Address address, String sellerUuid, BigDecimal discountAmount) {
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.orderUuid = UUID.randomUUID().toString();
        this.sellerUuid = sellerUuid;
        this.address = address;
        this.discountAmount = discountAmount;
    }

    public OrdersResponseDto toResponseDto() {

        return OrdersResponseDto.builder()
                .totalAmount(this.totalAmount)
                .status(this.status)
                .paymentMethod(this.paymentMethod)
                .sellerUuid(this.sellerUuid)
                .orderUuid(this.getOrderUuid())
                .build();
    }
    public void updateInfo(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
    public void orderUpdateStatus(Status status) {
        this.status = status;
    }
    public void isCancellable(Orders orders){
        if (orders.getStatus() != com.zinikai.shop.domain.order.entity.Status.ORDER_PENDING) {
            throw new IllegalArgumentException("Order is already confirmed");
        }

    }

}
