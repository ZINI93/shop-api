package com.zinikai.shop.domain.delivery.dto;

import com.zinikai.shop.domain.delivery.entity.Carrier;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeliveryRequestDto {


    private String orderUuid;
//    private String addressUuid;   // 이것도 불필요
//    private DeliveryStatus deliveryStatus;   // 불필요
    private String trackingNumber;
    private Carrier carrier;

    @Builder
    public DeliveryRequestDto(String orderUuid, String trackingNumber, Carrier carrier) {
        this.orderUuid = orderUuid;
//        this.addressUuid = addressUuid;
//        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
    }
}
