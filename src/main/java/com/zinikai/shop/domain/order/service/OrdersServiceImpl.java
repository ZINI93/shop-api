package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.zinikai.shop.domain.order.entity.QOrders.orders;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrdersResponseDto createOrder(Long memberId, OrdersRequestDto requestDto) {

        log.info("Creating order for member ID:{}", memberId);

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Not found member ID"));

        if (!Objects.equals(member.getId(), memberId)) {
            throw new IllegalArgumentException("MemberShip IDs do not match");
        }

        validateAmountAndPaymentMethod(requestDto.getTotalAmount(), requestDto.getPaymentMethod());

        Orders orders = Orders.builder()
                .member(member)
                .totalAmount(requestDto.getTotalAmount())
                .status(Status.PENDING)
                .paymentMethod(requestDto.getPaymentMethod())
                .build();

        Orders savedOrders = ordersRepository.save(orders);

        log.info("Created order: ID={}, MemberID={}, Amount={}",
                savedOrders.getId(), savedOrders.getMember().getId(), savedOrders.getTotalAmount());

        for (OrderItemRequestDto itemDto : requestDto.getOrderItems() ) {
            orderItemService.createAndSaveOrderItem(member ,itemDto,savedOrders);
        }
        return savedOrders.toResponseDto();

    }

    @Override
    public Page<OrdersResponseDto> searchOrder(String memberUuid, Status status, LocalDateTime starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {

        log.info("Searching orders for member UUID:{}", memberUuid);

        return ordersRepository.searchOrders(memberUuid, status, starDate, endDate, minAmount, maxAmount, sortField, pageable);
    }

    @Override
    @Transactional
    public OrdersResponseDto updateOrder(String memberUuid, String orderUuid, OrdersUpdateDto updateDto) {

        log.info("Updating order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders orders = ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, orderUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or order UUID "));


        matchMemberUuid(memberUuid, orders);

        validateAmountAndPaymentMethod(updateDto.getTotalAmount(), updateDto.getPaymentMethod());

        orders.UpdateInfo(updateDto.getTotalAmount(),
                updateDto.getStatus(),
                updateDto.getPaymentMethod());

        log.info("Updated order:{}", orders);

        return orders.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteOrder(String memberUuid, String orderUuid) {

        log.info("Deleting order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders orders = ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, orderUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or order UUID "));

        matchMemberUuid(memberUuid, orders);

        ordersRepository.delete(orders);
    }

    private void matchMemberUuid(String memberUuid, Orders orders) {
        if (!Objects.equals(orders.getMember().getMemberUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUId dose not match the order owner");
        }
    }

    private void validateAmountAndPaymentMethod(BigDecimal totalAmount, String paymentMethod) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater then or equal 0 en");
        }

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Please choose the payment method");
        }
    }
}
