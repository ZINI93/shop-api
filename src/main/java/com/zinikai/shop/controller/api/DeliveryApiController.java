package com.zinikai.shop.controller.api;


import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.delivery.service.DeliveryService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivers")
public class DeliveryApiController {

    private final DeliveryService deliveryService;
    private final OrdersRepository ordersRepository;

    @PostMapping
    ResponseEntity<DeliveryResponseDto> createDelivery(@Valid @RequestBody DeliveryRequestDto requestDto,
                                                       Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();


        Orders orders = ordersRepository.findByOrderUuid(requestDto.getOrderUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found order UUID"));

        String sellerUuid = orders.getSellerUuid();

        if (!memberUuid.equals(sellerUuid)){
            throw new IllegalArgumentException("Only the seller can create a delivery");
        }

        DeliveryResponseDto delivery = deliveryService.createDelivery(sellerUuid, requestDto);

        URI location = URI.create("/api/delivers" + delivery.getDeliveryUuid());
        return ResponseEntity.created(location).body(delivery);
    }

    @PutMapping("{deliveryUuid}/ship")
    ResponseEntity<DeliveryResponseDto> shippedDelivery(@PathVariable String deliveryUuid,
                                                        Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        DeliveryResponseDto delivery = deliveryService.shippedDelivery(memberUuid, deliveryUuid);

        return ResponseEntity.ok(delivery);

    }

    @GetMapping("{deliveryUuid}")
    ResponseEntity<DeliveryResponseDto> getDeliveryInfo(@PathVariable String deliveryUuid,
                                                        Authentication authentication) {
        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        DeliveryResponseDto deliveryInfo = deliveryService.getDeliveryInfo(memberUuid, deliveryUuid);

        return ResponseEntity.ok(deliveryInfo);
    }

    @PutMapping("{deliveryUuid}")
    ResponseEntity<DeliveryResponseDto> updateDelivery(@PathVariable String deliveryUuid,
                                                       Authentication authentication,
                                                       @RequestBody DeliveryUpdateDto updateDto) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        DeliveryResponseDto delivery = deliveryService.updateDelivery(memberUuid, deliveryUuid, updateDto);

        return ResponseEntity.ok(delivery);

    }

    @PutMapping("{deliveryUuid}/delivered")
    ResponseEntity<DeliveryResponseDto> confirmDelivery(@PathVariable String deliveryUuid,
                                                        Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        DeliveryResponseDto delivery = deliveryService.confirmDelivery(memberUuid, deliveryUuid);

        return ResponseEntity.ok(delivery);

    }

    private static CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }


}
