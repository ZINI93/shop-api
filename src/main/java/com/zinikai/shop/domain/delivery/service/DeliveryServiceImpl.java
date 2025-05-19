package com.zinikai.shop.domain.delivery.service;

import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.delivery.entity.Delivery;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import com.zinikai.shop.domain.delivery.exception.DeliveryNotFoundException;
import com.zinikai.shop.domain.delivery.exception.DeliveryStateMissMatchException;
import com.zinikai.shop.domain.delivery.repository.DeliveryRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.exception.OrderNotFoundException;
import com.zinikai.shop.domain.order.exception.OrderStateMissMatchException;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeliveryServiceImpl implements DeliveryService {
    private final OrdersRepository ordersRepository;

    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private static final BigDecimal SELLER_EARNINGS_PERCENTAGE = new BigDecimal("0.95");


    @Override
    public Delivery createDelivery(Member member, Orders order, DeliveryRequestDto requestDto) {

        return Delivery.builder()
                .orders(order)
                .address(order.getAddress())
                .deliveryStatus(DeliveryStatus.PENDING)
                .trackingNumber(requestDto.getTrackingNumber())
                .carrier(requestDto.getCarrier())
                .member(member) // 배송을 의뢰하는 판매자 정보
                .build();
    }

    @Override
    @Transactional
    public DeliveryResponseDto createDeliveryIfOrderCompleted(String memberUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for member UUID :{}", memberUuid);

        Orders orders = findOrderByOrderUuid(requestDto);
        Member member = findMemberByMemberUuid(memberUuid);

        validateOrderState(orders);
        Delivery delivery = createDelivery(member, orders, requestDto);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Created delivery UUID:{}", savedDelivery.getDeliveryUuid());

        return savedDelivery.toResponseDto();
    }

    @Override
    @Transactional
    public DeliveryResponseDto shipDelivery(String memberUuid, String deliveryUuid) {

        Delivery delivery = findDeliveryByMemberUuidAndDeliveryUuid(memberUuid, deliveryUuid);
        validateDeliveryStateIsPending(delivery);
        delivery.shippedDelivery(DeliveryStatus.SHIPPED);

        return delivery.toResponseDto();
    }

    @Override
    public DeliveryResponseDto getDeliveryInfo(String memberUuid, String deliveryUuid) {
        log.info("Searching delivery for member UUID:{}, delivery UUID:{}", memberUuid, deliveryUuid);

        Delivery delivery = findDeliveryByMemberUuidAndDeliveryUuid(memberUuid, deliveryUuid);
        return delivery.toResponseDto();
    }

    @Override
    @Transactional
    public DeliveryResponseDto updateDelivery(String memberUuid, String deliveryUuid, DeliveryUpdateDto updateDto) {

        log.info("Updating delivery for member UUID :{}, delivery UUID:{}", memberUuid, deliveryUuid);

        Delivery delivery = findDeliveryByMemberUuidAndDeliveryUuid(memberUuid, deliveryUuid);
        validateDeliveryStateIsPending(delivery);

        delivery.updateInfo(updateDto);

        log.info("Updated delivery UUID:{}", delivery.getDeliveryUuid());

        return delivery.toResponseDto();
    }

    @Override
    @Transactional
    public DeliveryResponseDto confirmDeliveryByBuyer(String buyerUuid, String deliveryUuid) {

        log.info("Confirming delivery for buyerUuid :{}, deliveryUuid:{}", buyerUuid, deliveryUuid);

        Delivery delivery = findDeliveryByBuyerUuidAndDeliveryUuid(buyerUuid, deliveryUuid);
        Member member = findMemberByMemberUuidInOrder(delivery.getOrders());

        validateDeliveryStateIsShipped(delivery);

        transferPaymentToSeller(delivery, delivery.getOrders(), member);

        log.info("Confirmed delivery UUID:{}", delivery.getDeliveryUuid());

        return delivery.toResponseDto();
    }

    private void transferPaymentToSeller(Delivery delivery, Orders order, Member seller) {
        delivery.confirmDelivery(LocalDateTime.now());
        BigDecimal sellerEarnings = order.sellerEarnings(SELLER_EARNINGS_PERCENTAGE);

        Member buyer = order.getMember();
        buyer.decreaseHoldBalance(order.getTotalAmount());
        seller.increaseBalance(sellerEarnings);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoConfirmDelivery() {
        log.info("Running scheduled task: Auto-confirm deliveries ");

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Delivery> pendingDeliveries = deliveryRepository.findOldDeliveries(DeliveryStatus.IN_TRANSIT, oneWeekAgo);

        if (pendingDeliveries.isEmpty()) {
            log.info("No deliveries to auto-confirm.");
            return;
        }

        pendingDeliveries.forEach(delivery -> {
            Member member = findMemberByMemberUuidInOrder(delivery.getOrders());
            transferPaymentToSeller(delivery, delivery.getOrders(), member);
            log.info("Auto-confirmed delivery UUID: {}", delivery.getDeliveryUuid());
        });

        log.info("Completed scheduled task: Auto-confirm deliveries");
    }

    private void validateDeliveryStateIsShipped(Delivery delivery) {
        if (!delivery.getDeliveryStatus().equals(DeliveryStatus.SHIPPED)) {
            throw new DeliveryStateMissMatchException("Delivery status is " + delivery.getDeliveryStatus()+ "Only completed delivery can have deliveries");
        }
    }

    private void validateOrderState(Orders orders) {
        if (orders.getStatus() != Status.ORDER_COMPLETED) {
            throw new OrderStateMissMatchException("Order status is" + orders.getStatus() + " Only completed orders can have deliveries");
        }
    }
    private static final EnumSet<DeliveryStatus> ALLOWED_STATUSES_FOR_UPDATE = EnumSet.of(DeliveryStatus.PENDING);

    private void validateDeliveryStateIsPending(Delivery delivery) {
        if (!ALLOWED_STATUSES_FOR_UPDATE.contains(delivery.getDeliveryStatus())) {
            throw new DeliveryStateMissMatchException("Delivery has not been created.");
        }
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("member UUID: member not found."));
    }

    private Orders findOrderByOrderUuid(DeliveryRequestDto requestDto) {
        return ordersRepository.findByOrderUuid(requestDto.getOrderUuid())
                .orElseThrow(() -> new OrderNotFoundException("Not found Order UUID"));
    }

    private Delivery findDeliveryByMemberUuidAndDeliveryUuid(String memberUuid, String deliveryUuid) {
        return deliveryRepository.findByMemberMemberUuidAndDeliveryUuid(memberUuid, deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("member UUID not found for owner UUID: " + memberUuid + ", delivery UUID: " + deliveryUuid));
    }

    private Member findMemberByMemberUuidInOrder(Orders orders) {
        return memberRepository.findByMemberUuid(orders.getSellerUuid())
                .orElseThrow(() -> new MemberNotFoundException("Member UUID: Member not found"));
    }


    private Delivery findDeliveryByBuyerUuidAndDeliveryUuid(String buyerUuid, String deliveryUuid) {
        return deliveryRepository.findByBuyerUuidAndDeliveryUuid(buyerUuid, deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("Not found BuyerUUID or delivery UUID"));
    }
}