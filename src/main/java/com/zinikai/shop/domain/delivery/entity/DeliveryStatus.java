package com.zinikai.shop.domain.delivery.entity;

public enum DeliveryStatus {
    PENDING,  //보류
    SHIPPED,  // 배달시작
    DELIVERED, // 배달완료
    IN_TRANSIT, // 운송중
    CANCELLED  //취소
}
