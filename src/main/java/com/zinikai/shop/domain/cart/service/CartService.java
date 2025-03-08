package com.zinikai.shop.domain.cart.service;


import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    CartResponseDto createCart(Long memberId, CartRequestDto requestDto);
    Page<CartResponseDto> getCarts(String ownerUuid, Pageable pageable);
    CartResponseDto updateCart(String ownerUuid, String cartUuid, CartUpdateDto updateDto);
    void deleteCart(String ownerUuid, String paymentUuid);
}
