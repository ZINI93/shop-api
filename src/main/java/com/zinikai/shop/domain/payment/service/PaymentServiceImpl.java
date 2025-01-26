package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    @Override @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {

        Orders orderId = ordersRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("オーダーがありません。"));

        Payment payment = Payment.builder()
                .orders(orderId)
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(requestDto.getPaymentMethod())
                .build();
        return paymentRepository.save(payment).toResponse();
    }
    @Override
    public PaymentResponseDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("paymentがありません"));
        return payment.toResponse();
    }

    @Override  //ADMIN用
    public List<PaymentResponseDto> getAllPayment() {
        List<Payment> payments = paymentRepository.findAll();
        if (payments.isEmpty()){
            throw new IllegalArgumentException("paymentがありません");
        }
        return payments.stream()
                .map(Payment::toResponse)
                .collect(Collectors.toList());
    }

    @Override @Transactional
    public PaymentResponseDto updatePayment(Long paymentId, PaymentUpdateDto updateDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("paymentがありません"));

        payment.updateInfo(updateDto.getStatus(), updateDto.getPaymentMethod());

//        return paymentRepository.save(payment).toResponse();
        return payment.toResponse();
    }

    @Override @Transactional
    public void deletePayment(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)){
            throw new EntityNotFoundException("paymentがありません");
        }
        paymentRepository.deleteById(paymentId);
    }
}
