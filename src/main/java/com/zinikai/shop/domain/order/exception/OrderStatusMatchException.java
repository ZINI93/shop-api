package com.zinikai.shop.domain.order.exception;

public class OrderStatusMatchException extends RuntimeException{

    public OrderStatusMatchException(){
    }

    public OrderStatusMatchException(String message){
        super(message);
    }
}
