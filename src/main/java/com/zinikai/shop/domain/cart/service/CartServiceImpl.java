package com.zinikai.shop.domain.cart.service;

import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.cart.repository.CartRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override @Transactional
    public CartResponseDto createCart(CartRequestDto requestDto) {

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("会員が登録をされていません。"));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品がありません。"));

        Cart cart = Cart.builder()
                .member(member)
                .product(product)
                .quantity(requestDto.getQuantity())
                .build();
        return  cartRepository.save(cart).toResponse();
    }
    @Override
    public CartResponseDto findById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("カートがありません"));
        return cart.toResponse();
    }

    @Override
    public List<CartResponseDto> getAllCart() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()){
            throw  new IllegalArgumentException("カートがありません");
        }
        return carts.stream()
                .map(Cart::toResponse)
                .collect(Collectors.toList());
    }

    @Override @Transactional
    public CartResponseDto updateCart(Long cartId, CartUpdateDto updateDto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("カートがありません"));
        cart.updateInfo(updateDto.getQuantity());
        return cart.toResponse();
    }

    @Override @Transactional
    public void deleteCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new EntityNotFoundException("カートがありません");
        }
        cartRepository.deleteById(cartId);
    }
}
