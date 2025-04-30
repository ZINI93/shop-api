package com.zinikai.shop.domain.product.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(){
        super("재고가 부족 합니다.");
    }
    public OutOfStockException(String message){
        super(message);
    }
    public OutOfStockException(String message, Throwable cause){
        super(message,cause);
    }
}
