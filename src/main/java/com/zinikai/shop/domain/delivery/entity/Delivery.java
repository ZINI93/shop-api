package com.zinikai.shop.domain.delivery.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.order.entity.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deliveries")
@Entity
public class Delivery extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Carrier carrier;

    @Column(name = "owner_uuid", nullable = false)
    private String ownerUuid;

    @Column(name = "buyer_uuid", nullable = false)
    private String buyerUuid;

    @Column(name = "delivery_uuid", nullable = false)
    private String deliveryUuid;

    @Column(name = "confirm_delivery")
    private LocalDateTime confirmDelivery;

    @Builder
    public Delivery(Orders orders, Address address, DeliveryStatus deliveryStatus, String trackingNumber, Carrier carrier, String ownerUuid, String buyerUuid, String deliveryUuid) {
        this.orders = orders;
        this.address = address;
        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.ownerUuid = ownerUuid;
        this.buyerUuid = orders.getMember() != null ? orders.getMember().getMemberUuid() : null;
        this.deliveryUuid = UUID.randomUUID().toString();
    }

    public DeliveryResponseDto toResponseDto() {
        return DeliveryResponseDto.builder()
                .orderUuid(this.orders.getOrderUuid())
                .addressUuid(this.address.getAddressUuid())
                .deliveryStatus(this.deliveryStatus)
                .trackingNumber(this.trackingNumber)
                .carrier(this.carrier)
                .buyerUuid(this.buyerUuid)
                .deliveryUuid(this.deliveryUuid)
                .build();
    }

    public void updateInfo(DeliveryUpdateDto updateDto) {
        this.carrier = updateDto.getCarrier();
        this.trackingNumber = updateDto.getTrackingNumber();
    }


    public void confirmDelivery(DeliveryStatus deliveryStatus, LocalDateTime confirmDelivery) {
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.confirmDelivery = confirmDelivery;
    }

    public void shippedDelivery(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
