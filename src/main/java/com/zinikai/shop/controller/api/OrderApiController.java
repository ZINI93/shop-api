package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrdersService orderService;

    @PostMapping
    public ResponseEntity<OrdersResponseDto> createOrder(@Valid @RequestBody OrdersRequestDto requestDto,
                                                         Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        OrdersResponseDto order = orderService.orderProcess(memberUuid, requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{orderUuid}")
                .buildAndExpand(order.getOrderUuid())
                .toUri();
        return ResponseEntity.created(location).body(order);

    }

    @PostMapping("/carts")
    public ResponseEntity<OrdersResponseDto> createOrderFromCart(@Valid @RequestBody OrdersRequestDto requestDto,
                                                                 Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        OrdersResponseDto order = orderService.orderProcessFromCart(memberUuid, requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{orderUuid}")
                .buildAndExpand(order.getOrderUuid())
                .toUri();

        return ResponseEntity.created(location).body(order);
    }

    @GetMapping("{orderUuid}")
    public ResponseEntity<OrdersResponseDto> getOrder(@PathVariable String orderUuid,
                                                      Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        OrdersResponseDto order = orderService.getOrder(memberUuid, orderUuid);

        return ResponseEntity.ok(order);

    }

    @GetMapping
    public ResponseEntity<Page<OrdersResponseDto>> searchOrder(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) LocalDateTime starDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            Authentication authentication
    ) {

        String memberUuid = getMemberUuid(authentication);

        Page<OrdersResponseDto> orders = orderService.searchOrder(memberUuid, status, starDate, endDate, minAmount, maxAmount, sortField, pageable);

        return ResponseEntity.ok(orders);

    }

    private String getMemberUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        return customUserDetails.getMemberUuid();
    }


    @PutMapping("{orderUuid}/cancel")
    public ResponseEntity<OrdersResponseDto> cancelOrder(@PathVariable String orderUuid,
                                                       Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        OrdersResponseDto orderUpdate = orderService.cancelOrder(memberUuid, orderUuid);
        return ResponseEntity.ok(orderUpdate);
    }

    @DeleteMapping("{orderUuid}")
    public ResponseEntity<OrdersResponseDto> deleteOrder(@PathVariable String orderUuId,
                                                         Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        orderService.deleteOrder(memberUuid, orderUuId);
        return ResponseEntity.noContent().build();
    }

    private CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
