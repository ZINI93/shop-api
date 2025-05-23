package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks PaymentServiceImpl paymentService;

    @Mock PaymentRepository paymentRepository;

    @Mock OrderItemRepository orderItemRepository;
    @Mock MailService mailService;

    @Mock OrdersRepository ordersRepository;

    @Mock MemberRepository memberRepository;

    PaymentRequestDto requestDto;
    Payment payment;
    Member member;
    Orders orders;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setMemberBalance(Member member, BigDecimal balance) throws Exception {
        Field field = member.getClass().getDeclaredField("balance");
        field.setAccessible(true);
        field.set(member, balance);
    }

    private void setOrdersId(Orders orders, Long id) throws Exception {
        Field field = orders.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(orders, id);
    }


    private void setPaymentId(Payment payment, Long id) throws Exception {
        Field field = payment.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(payment, id);
    }


    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().email("1234@naver.com").name("zini").memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);
        setMemberBalance(member,new BigDecimal(100000));

        orders = Orders.builder().member(Member.builder().memberUuid(UUID.randomUUID().toString()).build()).status(Status.ORDER_PENDING).paymentMethod("paypay").totalAmount(new BigDecimal(100.00)).orderUuid(UUID.randomUUID().toString()).build();
        setOrdersId(orders, 1L);

        requestDto = new PaymentRequestDto(
                1L
        );


        payment = new Payment(
                orders,
                PaymentStatus.PENDING,
                "PayPay",
                member,
                UUID.randomUUID().toString()
        );

        setPaymentId(payment,1L);


    }

    @Test
    void createPayment() {
        //given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        //when
        PaymentResponseDto result = paymentService.createPayment(member, orders);


        //then
        assertNotNull(result);
        assertEquals(payment.getPaymentMethod(), result.getPaymentMethod());
        assertEquals(payment.getPaymentMethod(), result.getPaymentMethod());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void confirmPayment(){

        //given
        when(paymentRepository.findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid())).thenReturn(Optional.ofNullable(payment));
        when(ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())).thenReturn(Optional.ofNullable(orders));

        //when
        PaymentResponseDto result = paymentService.confirmPayment(member.getMemberUuid(), payment.getPaymentUuid());
        System.out.println("balance:" + member.getBalance());
        System.out.println("amount:" + orders.getTotalAmount());
        //then
        assertNotNull(result);
        assertEquals(payment.getPaymentMethod(),result.getPaymentMethod());

        verify(paymentRepository,times(1)).findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid());
        verify(ordersRepository,times(1)).findByOrderUuid(payment.getOrders().getOrderUuid());
    }

    @Test
    void TestPayments() {
        //given
        PageRequest pageable = PageRequest.of(0, 10);

        List<Payment> mockData = List.of(payment);
        Page<Payment> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(paymentRepository.findAllByMemberMemberUuid(member.getMemberUuid(),pageable)).thenReturn(mockPage);
        //when
        Page<PaymentResponseDto> payments = paymentService.getPayments(member.getMemberUuid(), pageable);

        //then
        assertNotNull(payments);
        assertEquals(10, payments.getSize());

        verify(paymentRepository, times(1)).findAllByMemberMemberUuid(member.getMemberUuid(),pageable);

    }

    @Test
    void updatePayment() {

        //given
        when(paymentRepository.findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid())).thenReturn(Optional.ofNullable(payment));

        PaymentUpdateDto updatePayment = PaymentUpdateDto.builder()
                .paymentMethod("CASH")
                .status(PaymentStatus.REFUNDED)
                .build();

        //when
        PaymentResponseDto result = paymentService.updatePayment(member.getMemberUuid(),payment.getPaymentUuid(),updatePayment);

        //then
        assertNotNull(result);
        assertEquals(updatePayment.getPaymentMethod(), result.getPaymentMethod());
        assertEquals(updatePayment.getStatus(), result.getStatus());
        verify(paymentRepository,times(1)).findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid());
    }

    @Test
    void deletePayment() {
        //given
        when(paymentRepository.findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid())).thenReturn(Optional.ofNullable(payment));

        //when
        paymentService.deletePayment(member.getMemberUuid(), payment.getPaymentUuid());
        //then
        verify(paymentRepository,times(1)).findByMemberMemberUuidAndPaymentUuid(member.getMemberUuid(),payment.getPaymentUuid());
    }
}