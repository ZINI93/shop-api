package com.zinikai.shop.domain.product.exception;

public class ProductStatusNotMatchException extends RuntimeException{

    public ProductStatusNotMatchException (String message){
        super(message);
    }
}
