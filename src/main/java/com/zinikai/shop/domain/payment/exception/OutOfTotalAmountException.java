package com.zinikai.shop.domain.payment.exception;

public class OutOfTotalAmountException extends RuntimeException {
  public OutOfTotalAmountException(String message) {
    super(message);
  }
}
