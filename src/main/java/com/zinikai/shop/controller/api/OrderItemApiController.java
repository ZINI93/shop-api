package com.zinikai.shop.controller.api;


import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemApiController {

    private final OrderItemService orderItemService;


    @GetMapping
    public ResponseEntity<Page<OrderItemResponseDto>> getOrderItems(Authentication authentication,
                                                                    @PageableDefault(size = 10,page = 0)Pageable pageable){

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        Page<OrderItemResponseDto> orderItems = orderItemService.getOrderItems(memberUuid, pageable);

        return ResponseEntity.ok(orderItems);

    }

    @GetMapping("{orderItemUuid}")
    public ResponseEntity<OrderItemResponseDto> getOrderItem(Authentication authentication,
                                                             @PathVariable String orderItemUuid){

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        OrderItemResponseDto orderItem = orderItemService.getOrderItem(memberUuid,orderItemUuid);

        return ResponseEntity.ok(orderItem);
    }

}
