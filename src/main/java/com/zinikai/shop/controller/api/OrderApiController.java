package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrdersService orderService;

    //オーダーを作成
    @PostMapping
    public ResponseEntity<OrdersResponseDto> createOrder(@RequestBody OrdersRequestDto requestDto,
                                                         Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        Long memberId = customUserDetails.getMemberId();

        OrdersResponseDto order = orderService.createOrder(memberId, requestDto);
        URI location = URI.create("/api/orders" + order.getId());
        return ResponseEntity.created(location).body(order);

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

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<OrdersResponseDto> orders = orderService.searchOrder(memberUuid, status, starDate, endDate, minAmount, maxAmount, sortField, pageable);

        return ResponseEntity.ok(orders);

    }

    //オーダーをアップデート

    @PutMapping("{orderUuid}")
    public ResponseEntity<OrdersResponseDto> editOrder(@PathVariable String orderUuid,
                                                       @RequestBody OrdersUpdateDto updateDto,
                                                       Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        OrdersResponseDto orderUpdate = orderService.updateOrder(memberUuid, orderUuid, updateDto);
        return ResponseEntity.ok(orderUpdate);
    }
    //オーダーを削除

    @DeleteMapping("{orderUuid}")
    public ResponseEntity<OrdersResponseDto> deleteOrder(@PathVariable String orderUuId,
                                                         Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        orderService.deleteOrder(memberUuid, orderUuId);
        return ResponseEntity.noContent().build();
    }


    private CustomUserDetails getCustomUserDetails(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails;
    }
}
