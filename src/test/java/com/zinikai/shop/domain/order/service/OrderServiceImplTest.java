package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    OrdersRepository ordersRepository;

    @InjectMocks OrdersServiceImpl orderService;

    OrdersRequestDto ordersRequest;

    Orders orders;

    Member member;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setOrdersId(Orders orders, Long id) throws Exception {
        Field field = orders.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(orders, id);
    }

    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        orders = new Orders(
                member,
                new BigDecimal(10),
                Status.COMPLETED,
                "PayPay",
                UUID.randomUUID().toString()
        );
        setOrdersId(orders, 1L);

        ordersRequest = new OrdersRequestDto(
                orders.getMember().getId(),
                orders.getTotalAmount(),
                orders.getStatus(),
                orders.getPaymentMethod()
        );

    }

    @Test
    @DisplayName("オーダーを作成")
    void TestCreateOrder() {
        //given

        when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));
        when(ordersRepository.save(any(Orders.class))).thenReturn(orders);

        //when
        OrdersResponseDto result = orderService.createOrder(member.getId(),ordersRequest);


        //then
        assertNotNull(result);
        assertEquals(1L, orders.getId());
        assertEquals("PayPay", orders.getPaymentMethod());

        verify(ordersRepository, times(1)).save(any(Orders.class));
    }


    @Test
    @DisplayName("オーダーをサーチ")
    void TestSearch() {
        //given

        String sortFiled = "createAt";

        PageRequest pageable = PageRequest.of(0, 10);
        List<OrdersResponseDto> mockOrders = List.of(orders.toResponseDto());
        Page<OrdersResponseDto> mockOrder = new PageImpl<>(mockOrders, pageable, mockOrders.size());


        // 偽物データをRETURN設定
        Page<OrdersResponseDto> mockOrderpage = mock(Page.class);
        when(ordersRepository.searchOrders(
                member.getMemberUuid(),
                eq(orders.getStatus()),
                eq(LocalDateTime.now().minusDays(1)),
                eq(LocalDateTime.now()),
                eq(new BigDecimal(100)),
                eq(new BigDecimal(1000)),
                eq(sortFiled),
                eq(pageable)))
                .thenReturn(mockOrderpage);

        //when
        Page<OrdersResponseDto> result = orderService.searchOrder(
                member.getMemberUuid(),
                orders.getStatus(),
                orders.getCreatedAt(),
                orders.getUpdatedAt(),
                orders.getTotalAmount(),
                orders.getTotalAmount(),
                sortFiled,
                pageable);

        //then
        assertNotNull(result);
        assertEquals(mockOrderpage, result);
        verify(ordersRepository, times(1)).searchOrders(member.getMemberUuid(),
                eq(orders.getStatus()),
                eq(LocalDateTime.now().minusDays(1)),
                eq(LocalDateTime.now()),
                eq(new BigDecimal(100)),
                eq(new BigDecimal(1000)),
                eq(sortFiled),
                eq(pageable));

    }

    @Test
    @DisplayName("オーダーをアップデート")
    void TestOrderUpdate() {
        //given

        OrdersUpdateDto updateOrder = OrdersUpdateDto.builder()
                .totalAmount(ordersRequest.getTotalAmount())
                .paymentMethod(ordersRequest.getPaymentMethod())
                .status(Status.COMPLETED)
                .build();


        when(ordersRepository.findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid())).thenReturn(Optional.ofNullable(orders));

        //when
        OrdersResponseDto result = orderService.updateOrder(member.getMemberUuid(),orders.getOrderUuid(),updateOrder);

        //then
        assertNotNull(result);
        assertEquals(orders.getTotalAmount(), result.getTotalAmount());
        assertEquals(orders.getPaymentMethod(), result.getPaymentMethod());

        verify(ordersRepository, times(1)).findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid());
    }

    @Test
    @DisplayName("オーダーを削除")
    void TestDeleteOrder() {
        //given
        when(ordersRepository.findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid())).thenReturn(Optional.ofNullable(orders));

        //when
        orderService.deleteOrder(member.getMemberUuid(),orders.getOrderUuid());
        //then
        verify(ordersRepository, times(1)).findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid());
    }


}
