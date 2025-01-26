package com.zinikai.shop.domain.cart.service;


import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;

import java.util.List;

public interface CartService {

    CartResponseDto createCart(CartRequestDto requestDto);
    CartResponseDto findById(Long cartId);

    List<CartResponseDto> getAllCart();
    CartResponseDto updateCart(Long cartId, CartUpdateDto updateDto);
    void deleteCart(Long cartId);
}
