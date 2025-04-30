package com.zinikai.shop.domain.order.exception;


public class OrderNotFoundException extends RuntimeException{


    public OrderNotFoundException(){
        super("Not found Order");
    }


    public OrderNotFoundException(String memberUuid, String orderUuid){
        super(String.format("Order not found fro member Uuid: %s and Order Uuid: %s", memberUuid, orderUuid));
    }


    public OrderNotFoundException(String message){
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause){
        super(message, cause);
    }


}
