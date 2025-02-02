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
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock OrdersRepository ordersRepository;

    @InjectMocks
    OrdersServiceImpl orderService;

    OrdersRequestDto ordersRequest;

    Orders orders;
    @BeforeEach
    void setup(){
        ordersRequest = new OrdersRequestDto(
                Member.builder().id(1L).build().getId(),
                new BigDecimal(10),
                Status.COMPLETED,
                "PayPay"
        );

        orders = new Orders(
                1L,
                Member.builder().id(1L).build(),
                new BigDecimal(10),
                Status.COMPLETED,
                "PayPay"
        );

    }
    @Test
    @DisplayName("오더 생성 테스트")
    void TestCreateOrder(){
        //given
        Member memberId = Member.builder().id(1L).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberId));
        when(ordersRepository.save(any(Orders.class))).thenReturn(orders);

        //when
        OrdersResponseDto result = orderService.createOrder(ordersRequest);


        //then
        assertNotNull(result);
        assertEquals(1L,orders.getId());
        assertEquals("PayPay", orders.getPaymentMethod());

        verify(ordersRepository,times(1)).save(any(Orders.class));
    }

    @Test
    @DisplayName("IDで探す")
    void TestFindById(){
        //given

        when(ordersRepository.findById(1L)).thenReturn(Optional.ofNullable(orders));
        //when
        OrdersResponseDto result = orderService.getOrderById(1L);

        //then
        assertNotNull(result);
        assertEquals(orders.getMember().getId(),result.getMemberId());
        verify(ordersRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("オーダーをサーチ")
    void TestSearch(){
        //given

        PageRequest pageable = PageRequest.of(0, 10);

        Status status = Status.COMPLETED;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        BigDecimal minAmount = new BigDecimal(100);
        BigDecimal maxAmount = new BigDecimal(1000);
        String sortFiled = "createdAt";

        // 偽物データをRETURN設定
        Page<OrdersResponseDto> mockOrderpage = mock(Page.class);
        when(ordersRepository.searchOrders(eq(status),eq(startDate),eq(endDate),eq(minAmount),eq(maxAmount),eq(sortFiled),eq(pageable)))
                .thenReturn(mockOrderpage);


        //when
        Page<OrdersResponseDto> result = orderService.searchOrder(status, startDate, endDate, minAmount, maxAmount, sortFiled, pageable);

        //then
        assertNotNull(result);
        assertEquals(mockOrderpage,result);
        verify(ordersRepository,times(1)).searchOrders(eq(status),eq(startDate),eq(endDate),eq(minAmount),eq(maxAmount),eq(sortFiled),eq(pageable));

    }

    @Test
    @DisplayName("オーダーをアップデート")
    void TestOrderUpdate(){
        //given

        OrdersUpdateDto updateOrder = OrdersUpdateDto.builder()
                .totalAmount(ordersRequest.getTotalAmount())
                .paymentMethod(ordersRequest.getPaymentMethod())
                .status(Status.COMPLETED)
                .build();


        when(ordersRepository.findById(1L)).thenReturn(Optional.ofNullable(orders));
        when(ordersRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        OrdersResponseDto result = orderService.updateOrder(1L, updateOrder);

        //then
        assertNotNull(result);
        assertEquals(orders.getTotalAmount(),result.getTotalAmount());
        assertEquals(orders.getPaymentMethod(),result.getPaymentMethod());

        verify(ordersRepository,times(1)).findById(1L);
    }

    @Test
    @DisplayName("オーダーを削除")
    void TestDeleteOrder(){
        //given
        when(ordersRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ordersRepository).deleteById(1L);
        //when
        orderService.deleteOrder(1L);
        //then
        verify(ordersRepository,times(1)).deleteById(1L);
    }


}
