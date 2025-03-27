package com.zinikai.shop.domain.delivery.service;

import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.delivery.entity.Delivery;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import com.zinikai.shop.domain.delivery.repository.DeliveryRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Transactional
    public DeliveryResponseDto createDelivery(String ownerUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for owner UUID :{}", ownerUuid);

        Orders orders = ordersRepository.findByOrderUuid(requestDto.getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found Order UUID"));

        if (orders.getStatus() != Status.COMPLETED) {
            throw new IllegalArgumentException("Cannot create delivery for an order that is not completed.");
        }

        Delivery delivery = Delivery.builder()
                .orders(orders)
                .address(orders.getAddress())
                .deliveryStatus(DeliveryStatus.PENDING)
                .trackingNumber(requestDto.getTrackingNumber())
                .carrier(requestDto.getCarrier())
                .ownerUuid(ownerUuid)
                .build();

        log.info("Created delivery :{}", delivery);

        return deliveryRepository.save(delivery).toResponseDto();
    }

    @Override
    @Transactional
    public DeliveryResponseDto shippedDelivery(String ownerUuid, String deliveryUuid) {

        Delivery delivery = deliveryRepository.findByOwnerUuidAndDeliveryUuid(ownerUuid, deliveryUuid)
                .orElseThrow(() -> new IllegalArgumentException("owner UUID not found for owner UUID: " + ownerUuid + ", delivery UUID: " + deliveryUuid));

        if (delivery.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new IllegalArgumentException("Delivery has not been created.");
        }
        delivery.shippedDelivery(DeliveryStatus.SHIPPED);

        return delivery.toResponseDto();
    }

    @Override
    public DeliveryResponseDto getDeliveryInfo(String memberUuid, String deliveryUuid) {

        log.info("Searching delivery for member UUID:{}, delivery UUID:{}", memberUuid, deliveryUuid);

        Delivery delivery = deliveryRepository.findByMemberUuidAndDeliveryUuid(memberUuid, deliveryUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or delivery UUID"));

        return delivery.toResponseDto();
    }

    @Override
    @Transactional
    public DeliveryResponseDto updateDelivery(String ownerUuid, String deliveryUuid, DeliveryUpdateDto updateDto) {

        log.info("Updating delivery for owner UUID :{}, delivery UUID:{}", ownerUuid, deliveryUuid);

        Delivery delivery = deliveryRepository.findByOwnerUuidAndDeliveryUuid(ownerUuid, deliveryUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or delivery UUID"));

        if (delivery.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new IllegalArgumentException("Delivery is already confirmed");
        }

        delivery.updateInfo(updateDto);

        log.info("Updated delivery");

        return delivery.toResponseDto();
    }


    @Override
    @Transactional
    public DeliveryResponseDto confirmDelivery(String buyerUuid, String deliveryUuid) {

        log.info("Confirming delivery for buyerUuid :{}, deliveryUuid:{}", buyerUuid, deliveryUuid);

        Delivery delivery = deliveryRepository.findByBuyerUuidAndDeliveryUuid(buyerUuid, deliveryUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found BuyerUUID or delivery UUID"));

        Orders orders = ordersRepository.findByOrderUuid(delivery.getOrders().getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found buyer UUID"));

        Member member = memberRepository.findByMemberUuid(orders.getSellerUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID"));

        if (!delivery.getDeliveryStatus().equals(DeliveryStatus.SHIPPED)) {
            throw new IllegalArgumentException("It hasn't been delivered.");
        }

        delivery.confirmDelivery(DeliveryStatus.DELIVERED, LocalDateTime.now());


        BigDecimal sellerEarnings = orders.getTotalAmount().multiply(SELLER_EARNINGS_PERCENTAGE);

        orders.getMember().decreaseHoldBalance(orders.getTotalAmount());
        member.increaseBalance(sellerEarnings);


        log.info("Confirmed delivery");

        return delivery.toResponseDto();
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
            delivery.confirmDelivery(DeliveryStatus.DELIVERED, LocalDateTime.now());
            log.info("Auto-confirmed delivery UUID: {}", delivery.getDeliveryUuid());
        });

        log.info("Completed scheduled task: Auto-confirm deliveries");
    }

}