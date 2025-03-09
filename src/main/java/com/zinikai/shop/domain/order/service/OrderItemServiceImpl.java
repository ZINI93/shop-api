package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public void createAndSaveOrderItem(Member member, OrderItemRequestDto itemDto, Orders orders) {

        log.info("Creating orderItem for member email:{}", member.getEmail());

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Not found product id"));

        OrderItem orderItem = OrderItem.builder()
                .orders(orders)
                .product(product)
                .quantity(itemDto.getQuantity())
                .price(product.getPrice())
                .ownerUuid(member.getMemberUuid())
                .build();

        log.info("Created orderItems:{}", orderItem);

        orderItemRepository.save(orderItem);
    }

    @Override
    public Page<OrderItemResponseDto> getOrderItems(String ownerUuid, Pageable pageable) {

        log.info("Searching orderItem for member UUID:{}", ownerUuid);

        Page<OrderItem> orderItems = orderItemRepository.findAllByOwnerUuidOrderByCreatedAtDesc(ownerUuid, pageable);

        return orderItems.map(OrderItem::toResponseDto);

    }

    @Override
    public OrderItemResponseDto getOrderItem(String memberUuid, String orderItemUuid) {

        OrderItem orderItem = orderItemRepository.findByOwnerUuidAndOrderItemUuid(memberUuid,orderItemUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or orderItem UUID"));

        matchMemberUuid(memberUuid,orderItem);

        return orderItem.toResponseDto();

    }

    private void matchMemberUuid(String memberUuid, OrderItem orderItem) {
        if (!Objects.equals(orderItem.getOwnerUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUID dose not match the order owner");
        }
    }

}

