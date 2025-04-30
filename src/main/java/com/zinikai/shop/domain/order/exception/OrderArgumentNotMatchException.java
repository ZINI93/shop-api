package com.zinikai.shop.domain.order.exception;

import com.zinikai.shop.domain.order.entity.Orders;

public class OrderArgumentNotMatchException extends RuntimeException{


    public OrderArgumentNotMatchException(){

    }

    public OrderArgumentNotMatchException(String message){
        super(message);
    }

}
