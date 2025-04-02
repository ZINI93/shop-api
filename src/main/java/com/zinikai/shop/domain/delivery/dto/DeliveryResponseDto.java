package com.zinikai.shop.domain.delivery.dto;

import com.zinikai.shop.domain.delivery.entity.Carrier;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DeliveryResponseDto {

    private String orderUuid;
    private String addressUuid;
    private DeliveryStatus deliveryStatus;
    private String trackingNumber;
    private Carrier carrier;
    private String buyerUuid;
    private String deliveryUuid;


    @Builder
    public DeliveryResponseDto(String orderUuid, String addressUuid, DeliveryStatus deliveryStatus, String trackingNumber, Carrier carrier, String buyerUuid, String deliveryUuid) {
        this.orderUuid = orderUuid;
        this.addressUuid = addressUuid;
        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.buyerUuid = buyerUuid;
        this.deliveryUuid = deliveryUuid;
    }
}

