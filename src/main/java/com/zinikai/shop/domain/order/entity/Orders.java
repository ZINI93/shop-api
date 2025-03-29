package com.zinikai.shop.domain.order.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders extends TimeStamp { // order은 mysql 예약어 이기 때문에 table을 생성하지 못한다;;;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

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

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Builder
    public Orders(Member member, BigDecimal totalAmount, Status status, String paymentMethod, String orderUuid, Address address, String sellerUuid) {
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.orderUuid = UUID.randomUUID().toString();
        this.sellerUuid = sellerUuid;
        this.address = address;
    }

    public OrdersResponseDto toResponseDto() {

        return OrdersResponseDto.builder()
                .totalAmount(this.totalAmount)
                .status(this.status)
                .paymentMethod(this.paymentMethod)
                .build();
    }
    public void updateInfo(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
    public void ordersStatus(Status status) {
        this.status = status;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrders(this);
    }
}
