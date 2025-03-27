package com.zinikai.shop.domain.delivery.service;

import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;

public interface DeliveryService {

    DeliveryResponseDto createDelivery(String memberUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto shippedDelivery(String ownerUuid, String deliveryUuid);
    DeliveryResponseDto getDeliveryInfo(String memberUuid, String deliveryUuid);
    DeliveryResponseDto updateDelivery(String ownerUuid, String deliveryUuid, DeliveryUpdateDto updateDto);
    DeliveryResponseDto confirmDelivery(String buyerUuid, String deliveryUuid);

}
