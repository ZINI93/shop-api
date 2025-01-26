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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock CartRepository cartRepository;

    @Mock MemberRepository memberRepository;

    @Mock ProductRepository productRepository;
    @InjectMocks CartServiceImpl cartService;

    CartRequestDto requestDto;

    Cart cart;
    @BeforeEach
     void setup(){

       Long memberId = 1L;
       Long productId = 10L;

        requestDto = new CartRequestDto(memberId, productId, 10);

    }


    @Test
    @DisplayName("カートをに商品を追加します")
    void createCart(){
       //given
        Member member = Member.builder().id(1L).build();
        Product product = Product.builder().id(10L).build();

        Cart cart = new Cart(1L, member, product, 10);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        //when

        CartResponseDto result = cartService.createCart(requestDto);

        //then
        assertNotNull(result);
        assertEquals(1L,result.getMemberId());
        assertEquals(10,result.getQuantity());
        verify(cartRepository,times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("idで探す")
    void TestFindById(){
        //given
        Member member = Member.builder().id(1L).build();
        Product product = Product.builder().id(10L).build();

        Cart cart = new Cart(1L, member, product, 10);

        when(cartRepository.findById(1L)).thenReturn(Optional.ofNullable(cart));

        //when
        CartResponseDto result = cartService.findById(1L);

        //then
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository,times(1)).findById(1L);
    }
    @Test
    @DisplayName("カートをアップデート")
    void TestCartUpdate(){
        //given
        Member member = Member.builder().id(1L).build();
        Product product = Product.builder().id(10L).build();

        Cart cart = new Cart(1L, member, product, 10);

        // 20개로 수정
        CartUpdateDto cartUpdateDto = new CartUpdateDto(20);

        //업테이트 된 카트 객체
        Cart updateCart = new Cart(1L, member, product, 20);

        when(cartRepository.findById(1L)).thenReturn(Optional.ofNullable(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(updateCart);


        //when
        CartResponseDto result = cartService.updateCart(1L, cartUpdateDto);

        //then
        assertNotNull(result);
        assertEquals(20,result.getQuantity());
        verify(cartRepository,times(1)).save(any(Cart.class));
    }


    @Test
    @DisplayName("カートを削除")
    void deleteCart(){
        //given
        when(cartRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cartRepository).deleteById(1L);

        //when
        cartService.deleteCart(1L);

        //then
        verify(cartRepository,times(1)).deleteById(1L);

    }


}