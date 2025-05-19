package com.zinikai.shop.domain.delivery.service;

import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.delivery.entity.Delivery;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Orders;

public interface DeliveryService {

    Delivery createDelivery(Member member, Orders order, DeliveryRequestDto requestDto);
    DeliveryResponseDto createDeliveryIfOrderCompleted(String memberUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto shipDelivery(String ownerUuid, String deliveryUuid);
    DeliveryResponseDto getDeliveryInfo(String memberUuid, String deliveryUuid);
    DeliveryResponseDto updateDelivery(String ownerUuid, String deliveryUuid, DeliveryUpdateDto updateDto);
    DeliveryResponseDto confirmDeliveryByBuyer(String buyerUuid, String deliveryUuid);

}
