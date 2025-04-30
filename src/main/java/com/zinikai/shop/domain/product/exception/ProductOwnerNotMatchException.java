package com.zinikai.shop.domain.product.exception;

public class ProductOwnerNotMatchException extends RuntimeException{

    public ProductOwnerNotMatchException(String message){
        super(message);
    }
}
