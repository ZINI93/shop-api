package com.zinikai.shop.domain.cart.service;


import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    Cart createCart(Member member, Product product, CartRequestDto requestDto);
    CartResponseDto createCartWithValidate(String memberUuid, CartRequestDto requestDto);
    Page<CartResponseDto> getCarts(String ownerUuid, Pageable pageable);
    CartResponseDto updateCart(String ownerUuid, String cartUuid, CartUpdateDto updateDto);
    void deleteCart(String ownerUuid, String cartUuid);
    void validateCarts(List<Cart> carts, String sellerUuid);
}
