package com.zinikai.shop.domain.delivery.dto;

import com.zinikai.shop.domain.delivery.entity.Carrier;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class DeliveryUpdateDto {

    private Carrier carrier;
    private String trackingNumber;


    @Builder
    public DeliveryUpdateDto(Carrier carrier, String trackingNumber) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
    }
}
