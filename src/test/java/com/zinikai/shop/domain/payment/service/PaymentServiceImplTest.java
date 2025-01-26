package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.order.service.OrdersService;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import org.hibernate.sql.ast.tree.expression.CaseSimpleExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks PaymentServiceImpl paymentService;
    @Mock PaymentRepository paymentRepository;

    @Mock OrdersRepository ordersRepository;

    PaymentRequestDto requestDto;
    Payment testPayment;

    Orders orderId;
    @BeforeEach
    void setup(){

        Long paymentId = 1L;
        orderId = Orders.builder().id(2L).build();

        requestDto = new PaymentRequestDto(2L, PaymentStatus.COMPLETED, "PayPay" );

        testPayment = new Payment(paymentId, orderId ,requestDto.getStatus(),requestDto.getPaymentMethod());

    }

    @Test
    void createPayment(){
        //given

        when(ordersRepository.findById(2L)).thenReturn(Optional.of(orderId));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        //when
        PaymentResponseDto result = paymentService.createPayment(requestDto);

        //then
        assertNotNull(result);
        assertEquals(2L, result.getOrderId());
        assertEquals("PayPay",result.getPaymentMethod());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void TestFindById(){
        //given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        //when
        PaymentResponseDto result = paymentService.getPaymentById(1L);

        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(paymentRepository,times(1)).findById(1L);

    }


    @Test
    void TestFindAll(){

        //given
        List<Payment> paymentList = Collections.singletonList(testPayment);
        when(paymentRepository.findAll()).thenReturn(List.of(testPayment));
        //when
        List<PaymentResponseDto> result = paymentService.getAllPayment();

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void updatePayment(){

        //given
        PaymentUpdateDto paymentUpdateDto = new PaymentUpdateDto(PaymentStatus.PENDING, requestDto.getPaymentMethod());
//        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        //when
        PaymentResponseDto result = paymentService.updatePayment(1L, paymentUpdateDto);

        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(PaymentStatus.PENDING,result.getStatus());
//        verify(paymentRepository,times(1)).save(any(Payment.class));
    }

    @Test
    void deletePayment(){
        //given
        when(paymentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(1L);

        //when
        paymentService.deletePayment(1L);
        //then
        verify(paymentRepository, times(1)).deleteById(1L);
    }
}