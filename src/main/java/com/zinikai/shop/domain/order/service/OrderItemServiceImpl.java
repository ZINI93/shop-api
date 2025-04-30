package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderItem createAndSaveOrderItem(Member member, OrderItemRequestDto itemDto, Orders orders, Product product) {

        log.info("Creating orderItem for member email:{}", member.getEmail());

        return OrderItem.builder()
                .orders(orders)
                .product(product)
                .quantity(itemDto.getQuantity())
                .price(product.getPrice())
                .ownerUuid(member.getMemberUuid())
                .build();

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

    @Override
    public Page<OrderItemResponseDto> getSalesHistory(String ownerUuid, Pageable pageable) {

        Page<OrderItem> salesProducts = orderItemRepository.findByProductMemberMemberUuid(ownerUuid, pageable);

        return salesProducts.map(OrderItem::toResponseDto);
    }

    @Override
    public void refundStockByOrder(Orders order) {

        List<OrderItem> orderItems = orderItemRepository.findByOrders(order);

        orderItems.forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.refundStock(orderItem.getQuantity());
        });

    }

    @Override
    public void decreaseStockByOrderItem(OrdersRequestDto requestDto) {

        List<String> productUuids = requestDto.getOrderItems().stream().map(OrderItemRequestDto::getProductUuid).collect(Collectors.toList());

        Map<String, Product> productMap = productRepository.findAllByProductUuidIn(productUuids).stream().collect(Collectors.toMap(Product::getProductUuid, Function.identity()));

        requestDto.getOrderItems().forEach(orderItem -> {
            String productUuid = orderItem.getProductUuid();
            Product product = productMap.get(productUuid);
            product.decreaseStock(orderItem.getQuantity());
        });
    }

    private void matchMemberUuid(String memberUuid, OrderItem orderItem) {
        if (!Objects.equals(orderItem.getOwnerUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUID dose not match the order owner");
        }
    }

}

