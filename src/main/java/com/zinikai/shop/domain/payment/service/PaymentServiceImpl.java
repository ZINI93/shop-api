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
import com.zinikai.shop.domain.payment.exception.OutOfTotalAmountException;
import com.zinikai.shop.domain.payment.exception.PaymentNotFoundException;
import com.zinikai.shop.domain.payment.exception.StateMissMatchException;
import com.zinikai.shop.domain.payment.exception.ValidatePaymentException;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.exception.ProductNotFoundException;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ProductImageRepository productImageRepository;

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MailService mailService;

    @Override
    public PaymentResponseDto createPayment(Member member, Orders orders) {

        log.info("Creating payment for member:{}, order:{}", member.getMemberUuid(), orders.getOrderUuid());

        validatePaymentMethod(orders.getPaymentMethod());

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
        validatePaymentStatusAndOrderStatusIsPending(payment, order);

        BigDecimal totalAmount = order.getTotalAmount();
        Member member = order.getMember();
        validateTotalAmount(member, totalAmount);

        payment.paymentStatus(PaymentStatus.COMPLETED);
        order.orderUpdateStatus(Status.ORDER_COMPLETED);

        member.decreaseBalance(totalAmount);
        member.increaseHoldBalance(totalAmount);

        mailService.sendPaymentCompletedEmail(
                order.getMember().getEmail(),
                order.getMember().getName(),
                order.getOrderUuid(),
                order.getTotalAmount(),
                order.getPaymentMethod());

        return payment.toResponse();
    }

    @Override @Transactional
    public PaymentResponseDto cancelPayment(String memberUuid, String paymentUuid) {

        log.info("Canceling payment: member UUID={}, payment UUID={}", memberUuid, paymentUuid);

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);
        Orders order = findOrderByOrderUuid(payment);
        validatePaymentStatusAndOrderStatusIsPending(payment, order);

        payment.paymentStatus(PaymentStatus.FAILED);
        order.orderUpdateStatus(Status.ORDER_CANCELLED);

        log.info("Payment and order canceled: paymentUuid={}, orderUuid={}", paymentUuid, order.getOrderUuid());

        return payment.toResponse();

    }


    @Override
    public Page<PaymentResponseDto> getPayments(String memberUuid, Pageable pageable) {

        log.info("Searching payment for owner UUID :{}", memberUuid);

        Page<Payment> payments = paymentRepository.findAllByMemberMemberUuid(memberUuid, pageable);

        return payments.map(Payment::toResponse);

    }

    @Override
    public PaymentResponseDto getPayment(String memberUuid, String paymentUuid) {

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);

        return payment.toResponse();
    }

    private static final Set<PaymentStatus> REFUNDABLE_STATUSES = EnumSet.of(PaymentStatus.REFUNDED);

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(String memberUuid, String paymentUuid, PaymentUpdateDto updateDto) {

        log.info("Updating payment for Owner UUID :{}, Payment UUID:{}", memberUuid, paymentUuid);

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);
        validatePaymentStatusIsPending(payment);

        payment.updateInfo(updateDto.getStatus(), updateDto.getPaymentMethod());

        Orders orders = validateExistedOrder(paymentUuid, payment);

        List<OrderItem> orderItems = orderItemRepository.findByOrders(orders);

        orderItems.forEach(orderItem -> {
            Product product = orderItem.getProduct();

            if (product == null) {
                throw new ProductNotFoundException("Product not found for orderItem");
            }

            if (REFUNDABLE_STATUSES.contains(updateDto.getStatus())) {
                product.refundStock(orderItem.getQuantity());
            } else {
                throw new ValidatePaymentException("No Refund has been made");
            }

            if (updateDto.getStatus() == PaymentStatus.COMPLETED) {
                orders.orderUpdateStatus(Status.ORDER_COMPLETED);
            }

        });

        log.info("updated payment:{}", QPayment.payment);

        return payment.toResponse();
    }

    @Override
    @Transactional
    public void deletePayment(String memberUuid, String paymentUuid) {

        log.info("Deleting payment for owner UUID:{}", memberUuid);

        Payment payment = findPaymentByMemberUuidAndPaymentUuid(memberUuid, paymentUuid);

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
            throw new ValidatePaymentException("Please choose the payment method");
        }
    }

    private Orders validateExistedOrder(String paymentUuid, Payment payment) {
        Orders orders = payment.getOrders();
        if (orders == null) {
            throw new OrderNotFoundException("Order not found for payment: " + paymentUuid);
        }
        return orders;
    }

    private void validatePaymentStatusAndOrderStatusIsPending(Payment payment, Orders order) {
        validatePaymentStatusIsPending(payment);
        validateOrderStatusIsPending(order);
    }

    private void validatePaymentStatusIsPending(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new StateMissMatchException("Payment is already confirmed");
        }
    }

    private void validateOrderStatusIsPending(Orders order){
        if (order.getStatus() != Status.ORDER_PENDING) {
            throw new StateMissMatchException("Order is already confirmed");
        }
    }

    private void validateTotalAmount(Member member, BigDecimal totalAmount) {
        if (member.getBalance().compareTo(totalAmount) > 0) {
            throw new OutOfTotalAmountException("Insufficient balance");
        }
    }


    private Orders findOrderByOrderUuid(Payment payment) {
        return ordersRepository.findByOrderUuid(payment.getOrders().getOrderUuid())
                .orElseThrow(() -> new OrderNotFoundException("Order UUID: order Not found"));
    }

    private Payment findPaymentByMemberUuidAndPaymentUuid(String memberUuid, String paymentUuid) {
        return paymentRepository.findByMemberMemberUuidAndPaymentUuid(memberUuid, paymentUuid)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for owner UUID: " + memberUuid + ", payment UUID: " + paymentUuid));
    }
}
