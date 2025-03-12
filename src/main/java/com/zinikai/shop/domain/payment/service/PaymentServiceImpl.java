package com.zinikai.shop.domain.payment.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.entity.QPayment;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zinikai.shop.domain.payment.entity.QPayment.payment;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final MailService mailService;


    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long memberId, PaymentRequestDto requestDto) {

        log.info("Creating payment for member ID:{}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + memberId));

        Orders order = ordersRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("order not found with ID: " + requestDto.getOrderId()));

        Payment payment = Payment.builder()
                .orders(order)
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(order.getPaymentMethod())
                .ownerUuid(member.getMemberUuid())
                .build();

        log.info("Created payment:{}", payment);


        List<OrderItem> orderItems = orderItemRepository.findByOrders(order);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();


            if (product.getStock() < orderItem.getQuantity()) {
                throw new IllegalArgumentException("Stock shortage! product name: " + product.getName());
            }

            product.decreaseStock(orderItem.getQuantity());

            BigDecimal totalPrice = (product.getPrice()).multiply(new BigDecimal(orderItem.getQuantity()));
            // 手数料５％
            BigDecimal sellerEarnings = totalPrice.multiply(new BigDecimal("0.95"));
            member.increaseBalance((sellerEarnings));

        }

        if (PaymentStatus.COMPLETED.equals(payment.getStatus())) {
            mailService.sendPaymentCompletedEmail(
                    order.getMember().getEmail(),
                    order.getMember().getName(),
                    order.getOrderUuid(),
                    order.getTotalAmount(),
                    order.getPaymentMethod());
        } else {
            throw new IllegalArgumentException("Check completed");
        }

        return paymentRepository.save(payment).toResponse();

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

        matchOwnerUuid(ownerUuid, payment);

        payment.updateInfo(updateDto.getStatus(), updateDto.getPaymentMethod());

        log.info("updated payment:{}", payment);

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
        }

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

    private static void matchOwnerUuid(String ownerUuid, Payment payment) {
        if (!Objects.equals(payment.getOwnerUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Owner UUID not match payment owner");
        }
    }
}
