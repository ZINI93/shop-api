package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import com.zinikai.shop.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MailService mailService;

    @Override @Transactional
    public PaymentResponseDto confirmPayment(String ownerUuid, String paymentUuid) {

        Payment payment = paymentRepository.findByOwnerUuidAndPaymentUuid(ownerUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + ownerUuid + ", payment UUID: " + paymentUuid));

        Orders order = ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("order not found with ID: " + payment.getOrders().getOrderUuid()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already confirmed");
        }

        if (order.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Order is already confirmed");
        }

        BigDecimal totalAmount = order.getTotalAmount();
        Member member = order.getMember();
        if (member.getBalance().compareTo(totalAmount) < 0){
            throw new IllegalArgumentException("Insufficient balance");
        }

            payment.paymentStatus(PaymentStatus.COMPLETED);
            order.ordersStatus(Status.COMPLETED);

        List<OrderItem> orderItems = orderItemRepository.findByOrders(order);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            if (product.getStock() < orderItem.getQuantity()) {
                throw new IllegalArgumentException("Stock shortage! product name: " + product.getName());
            }
        }

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.decreaseStock(orderItem.getQuantity());
        }

        BigDecimal totalPrice = order.getTotalAmount();

        order.getMember().decreaseBalance(totalPrice);
        order.getMember().increaseHoldBalance(totalPrice);

        mailService.sendPaymentCompletedEmail(
                order.getMember().getEmail(),
                order.getMember().getName(),
                order.getOrderUuid(),
                order.getTotalAmount(),
                order.getPaymentMethod());

        return payment.toResponse();
    }

    @Override @Transactional
    public PaymentResponseDto cancelPayment(String ownerUuid, String paymentUuid) {

        log.info("Canceling payment: ownerUuid={}, paymentUuid={}", ownerUuid, paymentUuid);

        Payment payment = paymentRepository.findByOwnerUuidAndPaymentUuid(ownerUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + ownerUuid + ", payment UUID: " + paymentUuid));

        Orders order = ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("order not found with ID: " + payment.getOrders().getOrderUuid()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already confirmed");
        }

        if (order.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Order is already confirmed");
        }

        payment.paymentStatus(PaymentStatus.FAILED);
        order.ordersStatus(Status.CANCELLED);

        log.info("Payment and order canceled: paymentUuid={}, orderUuid={}", paymentUuid, order.getOrderUuid());

        return payment.toResponse();

    }


    @Override
    public Page<PaymentResponseDto> getPayments(String ownerUuid, Pageable pageable) {

        log.info("Searching payment for owner UUID :{}", ownerUuid);

        Page<Payment> payments = paymentRepository.findAllByOwnerUuid(ownerUuid, pageable);

        return payments.map(Payment::toResponse);

    }

    @Override
    public PaymentResponseDto getPayment(String ownerUuid, String paymentUuid) {

        Payment payment = paymentRepository.findByOwnerUuidAndPaymentUuid(ownerUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + ownerUuid + ", payment UUID: " + paymentUuid));

        matchOwnerUuid(ownerUuid, payment);

        return payment.toResponse();
    }

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(String ownerUuid, String paymentUuid, PaymentUpdateDto updateDto) {

        log.info("Updating payment for Owner UUID :{}, Payment UUID:{}", ownerUuid, paymentUuid);

        Payment payment = paymentRepository.findByOwnerUuidAndPaymentUuid(ownerUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + ownerUuid + ", payment UUID: " + paymentUuid));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already confirmed");
        }

        matchOwnerUuid(ownerUuid, payment);

        payment.updateInfo(updateDto.getStatus(), updateDto.getPaymentMethod());

        Orders orders = payment.getOrders();
        if (orders == null) {
            throw new IllegalStateException("Order not found for payment: " + paymentUuid);
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrders(orders);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            if (product == null) {
                throw new IllegalArgumentException("Product not found for orderItem");
            }

            if (updateDto.getStatus() == PaymentStatus.REFUNDED) {
                product.refundStock(orderItem.getQuantity());
            } else {
                throw new IllegalStateException("No Refund has been made");
            }

            if (updateDto.getStatus() == PaymentStatus.COMPLETED) {
                orders.ordersStatus(Status.COMPLETED);
            }
        }

        log.info("updated payment:{}", payment);

        return payment.toResponse();
    }

    @Override
    @Transactional
    public void deletePayment(String ownerUuid, String paymentUuid) {

        log.info("Deleting payment for owner UUID:{}", ownerUuid);

        Payment payment = paymentRepository.findByOwnerUuidAndPaymentUuid(ownerUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + ownerUuid + ", payment UUID: " + paymentUuid));

        matchOwnerUuid(ownerUuid, payment);

        paymentRepository.delete(payment);
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void autoCancelPendingPayments() {
        log.info("Running scheduled task: Auto-failed expired pending payments");

        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);

        int updateCount = paymentRepository.bulkFailedExpiredPayment(
                PaymentStatus.PENDING,
                PaymentStatus.FAILED,
                expirationTime
        );

        log.info("Failed {} expired orders", updateCount);

    }

    private static void matchOwnerUuid(String ownerUuid, Payment payment) {
        if (!Objects.equals(payment.getOwnerUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Owner UUID not match payment owner");
        }
    }
}
