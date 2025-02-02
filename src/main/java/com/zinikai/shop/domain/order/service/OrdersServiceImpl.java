package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.OrdersUpdateDto;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;

    @Override @Transactional
    public OrdersResponseDto createOrder(OrdersRequestDto requestDto) {

        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(() -> new IllegalArgumentException("会員が登録をされていません"));


        Orders orders = Orders.builder()
                .member(member)
                .totalAmount(requestDto.getTotalAmount())
                .status(Status.COMPLETED)
                .paymentMethod(requestDto.getPaymentMethod())
                .build();
        return ordersRepository.save(orders).toResponseDto();
    }

    @Override
    public OrdersResponseDto getOrderById(Long orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("オーダーがありません。"));
        return orders.toResponseDto();
    }

    @Override
    public Page<OrdersResponseDto> searchOrder(Status status, LocalDateTime starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {
        return ordersRepository.searchOrders(status, starDate,  endDate,  minAmount,  maxAmount, sortField, pageable);
    }

    @Override @Transactional
    public OrdersResponseDto updateOrder(Long orderId, OrdersUpdateDto updateDto) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("オーダーがありません。"));

        orders.UpdateInfo(updateDto.getTotalAmount(),
                updateDto.getStatus(),
                updateDto.getPaymentMethod());

        return ordersRepository.save(orders).toResponseDto();
    }

    @Override @Transactional
    public void deleteOrder(Long orderId) {
        if (!ordersRepository.existsById(orderId)){
            throw  new EntityNotFoundException("オーダーがありません");
        }

        ordersRepository.deleteById(orderId);

    }
}
