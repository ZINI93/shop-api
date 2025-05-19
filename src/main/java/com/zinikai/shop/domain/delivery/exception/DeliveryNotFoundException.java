package com.zinikai.shop.domain.delivery.exception;

public class DeliveryNotFoundException extends RuntimeException {
  public DeliveryNotFoundException(String message) {
    super(message);
  }
}
