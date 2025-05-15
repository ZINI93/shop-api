package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.exception.OrderNotFoundException;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.entity.QPayment;
import com.zinikai.shop.domain.payment.exception.StateMissMatchException;
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

    @Override
    public PaymentResponseDto createPayment(Member member, Orders orders) {

        log.info("Creating payment for member:{}, order:{}", member.getMemberUuid(), orders.getOrderUuid());

        Payment savedPayment = Payment.builder()
                .orders(orders)
                .status(PaymentStatus.PENDING)
                .paymentMethod(orders.getPaymentMethod())
                .member(member)
                .build();

        log.info("Created payment Uuid:{}", savedPayment.getPaymentUuid());

        return paymentRepository.save(savedPayment).toResponse();
    }

    @Override @Transactional
    public PaymentResponseDto confirmPayment(String memberUuid, String paymentUuid) {

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);
        Orders order = findOrderByOrderUuid(payment);
        validateState(payment, order);

        BigDecimal totalAmount = order.getTotalAmount();
        Member member = order.getMember();
        if (member.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        payment.paymentStatus(PaymentStatus.COMPLETED);
        order.orderUpdateStatus(Status.ORDER_COMPLETED);

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

    private void validateState(Payment payment, Orders order) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new StateMissMatchException("Payment is already confirmed");
        }

        if (order.getStatus() != Status.ORDER_PENDING) {
            throw new StateMissMatchException("Order is already confirmed");
        }
    }

    private Orders findOrderByOrderUuid(Payment payment) {
        return ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())
                .orElseThrow(() -> new OrderNotFoundException("order Not found"));
    }

    private Payment findPaymentByMemberUuidAndPaymentUuid(String memberUuid, String paymentUuid) {
        return paymentRepository.findByMemberMemberUuidAndPaymentUuid(memberUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for owner UUID: " + memberUuid + ", payment UUID: " + paymentUuid));
    }

    @Override
    @Transactional
    public PaymentResponseDto cancelPayment(String memberUuid, String paymentUuid) {

        log.info("Canceling payment: member UUID={}, pyment UUID={}", memberUuid, paymentUuid);

        Payment payment = paymentRepository.findByMemberMemberUuidAndPaymentUuid(memberUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for memberUuid UUID: " + memberUuid + ", payment UUID: " + paymentUuid));

        Orders order = ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("order not found with ID: " + payment.getOrders().getOrderUuid()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already confirmed");
        }

        if (order.getStatus() != Status.ORDER_PENDING) {
            throw new IllegalArgumentException("Order is already confirmed");
        }

        payment.paymentStatus(PaymentStatus.FAILED);
        order.orderUpdateStatus(Status.ORDER_CANCELLED);

        log.info("Payment and order canceled: paymentUuid={}, orderUuid={}", paymentUuid, order.getOrderUuid());

        return payment.toResponse();

    }


    @Override
    public Page<PaymentResponseDto> getPayments(String ownerUuid, Pageable pageable) {

        log.info("Searching payment for owner UUID :{}", ownerUuid);

        Page<Payment> payments = paymentRepository.findAllByMemberMemberUuid(ownerUuid, pageable);

        return payments.map(Payment::toResponse);

    }

    @Override
    public PaymentResponseDto getPayment(String memberUuid, String paymentUuid) {

        Payment payment = paymentRepository.findByMemberMemberUuidAndPaymentUuid(memberUuid, paymentUuid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for member UUID: " + memberUuid + ", payment UUID: " + paymentUuid));

        matchOwnerUuid(memberUuid, payment);

        return payment.toResponse();
    }

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(String memberUuid, String paymentUuid, PaymentUpdateDto updateDto) {

        log.info("Updating payment for Owner UUID :{}, Payment UUID:{}", memberUuid, paymentUuid);

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already confirmed");
        }

        matchOwnerUuid(memberUuid, payment);

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
                orders.orderUpdateStatus(Status.ORDER_COMPLETED);
            }
        }

        log.info("updated payment:{}", QPayment.payment);

        return payment.toResponse();
    }

    @Override
    @Transactional
    public void deletePayment(String memberUuid, String paymentUuid) {

        log.info("Deleting payment for owner UUID:{}", memberUuid);

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);

        matchOwnerUuid(memberUuid, payment);

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

    @Override
    public void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Please choose the payment method");
        }
    }

    private static void matchOwnerUuid(String ownerUuid, Payment payment) {
        if (!Objects.equals(payment.getMember().getMemberUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Owner UUID not match payment owner");
        }
    }
}
