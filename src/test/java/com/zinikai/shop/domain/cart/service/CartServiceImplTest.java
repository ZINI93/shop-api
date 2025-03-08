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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private CartServiceImpl cartService;
    CartRequestDto cartRequest;
    Member member;

    Product product;
    Cart cart;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setProductId(Product product, Long id) throws Exception {
        Field field = product.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(product, id);
    }

    private void setCartId(Cart cart, Long id) throws Exception {
        Field field = cart.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(cart, id);
    }
    @BeforeEach
     void setup() throws Exception{

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member,1L);

        product = Product.builder().stock(10).build();
        setProductId(product,1L);

        cart = new Cart(member , product, 10, UUID.randomUUID().toString());
        setCartId(cart,1L);

        cartRequest = new CartRequestDto(cart.getMember().getId(),cart.getProduct().getId(),cart.getQuantity());
    }

    @Test
    @DisplayName("カートをに商品を追加します")
    void createCart(){
       //given

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        //when

        CartResponseDto result = cartService.createCart(member.getId(),cartRequest);

        //then
        assertNotNull(result);
        assertEquals(1L,result.getMemberId());
        assertEquals(10,result.getQuantity());
        assertNotEquals(-1, result.getQuantity());
        verify(cartRepository,times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("カートにある商品のリスト照会")
    void TestGetCarts(){
        //given
        PageRequest pageable = PageRequest.of(0, 10);
        List<CartResponseDto> mockCarts = List.of(cart.toResponse());
        Page<CartResponseDto> mockCartPage = new PageImpl<>(mockCarts, pageable, mockCarts.size());

        when(cartRepository.findAllByMemberMemberUuid(member.getMemberUuid(),pageable)).thenReturn(mockCartPage);

        //when
        Page<CartResponseDto> result = cartService.getCarts(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(mockCarts.size(),result.getTotalElements());
        verify(cartRepository,times(1)).findAllByMemberMemberUuid(member.getMemberUuid(),pageable);
    }
    @Test
    @DisplayName("カートをアップデート")
    void TestCartUpdate(){
        //given

        CartUpdateDto updatedCard = CartUpdateDto.builder().quantity(5).build();

        when(cartRepository.findByMemberMemberUuidAndCartUuid(member.getMemberUuid(), cart.getCartUuid())).thenReturn(Optional.ofNullable(cart));;

        //when
        CartResponseDto result = cartService.updateCart(member.getMemberUuid(),cart.getCartUuid(),updatedCard);

        //then
        assertNotNull(result);
        assertEquals(5,result.getQuantity());
        assertNotEquals(-1, result.getQuantity());
        verify(cartRepository,times(1)).findByMemberMemberUuidAndCartUuid(member.getMemberUuid(), cart.getCartUuid());
    }


    @Test
    @DisplayName("カートを削除")
    void deleteCart(){
        //given
        when(cartRepository.findByMemberMemberUuidAndCartUuid(member.getMemberUuid(),cart.getCartUuid())).thenReturn(Optional.ofNullable(cart));

        //when
        cartService.deleteCart(member.getMemberUuid(),cart.getCartUuid());

        //then
        verify(cartRepository,times(1)).delete(cart);

    }


}