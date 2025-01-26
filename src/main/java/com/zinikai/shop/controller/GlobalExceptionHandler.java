//package com.zinikai.shop.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(CartNotFoundException.class)
//    public ResponseEntity<String> handleCartNotFoundException(CartNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }
//}

// 고려할 사항