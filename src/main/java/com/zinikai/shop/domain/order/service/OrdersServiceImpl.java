package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import com.zinikai.shop.domain.payment.repository.PaymentRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemService orderItemService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public OrdersResponseDto createOrder(String memberUuid, OrdersRequestDto requestDto) {

        log.info("Creating order for member ID:{}", memberUuid);

        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID"));

        Address address = addressRepository.findByMemberMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID"));

        BigDecimal totalAmount = calculateTotalAmount(requestDto.getOrderItems());

        validateAmountAndPaymentMethod(totalAmount, requestDto.getPaymentMethod());


        //  一回のオーダーは一人の販売者が売ってる商品を購入が可能です。
        List<String> productIds = requestDto.getOrderItems().stream()
                .map(OrderItemRequestDto::getProductUuid)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllByProductUuidIn(productIds);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No valid products found for the given IDs");
        }

        String sellerUuid = products.get(0).getOwnerUuid();

        for (Product product : products) {
            if (!product.getOwnerUuid().equals(sellerUuid)) {
                throw new IllegalArgumentException("All items in the order must be from the same seller.");
            }
        }

        Orders orders = Orders.builder()
                .member(member)
                .totalAmount(totalAmount)
                .status(Status.PENDING)
                .paymentMethod(requestDto.getPaymentMethod())
                .sellerUuid(sellerUuid)
                .address(address)
                .build();

        Orders savedOrders = ordersRepository.save(orders);

        log.info("Created order: ID={}, MemberID={}, Amount={}",
                savedOrders.getId(), savedOrders.getMember().getId(), savedOrders.getTotalAmount());

        for (OrderItemRequestDto itemDto : requestDto.getOrderItems()) {
            orderItemService.createAndSaveOrderItem(member, itemDto, savedOrders);
        }

        Payment payment = Payment.builder()
                .orders(orders)
                .status(PaymentStatus.PENDING)
                .paymentMethod(orders.getPaymentMethod())
                .ownerUuid(member.getMemberUuid())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment:{}", savedPayment);

        return savedOrders.toResponseDto();
    }


    @Override
    public OrdersResponseDto getOrder(String memberUuid, String ordersUuid) {
        Orders order = ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, ordersUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or order UUID"));

        matchMemberUuid(memberUuid, order);

        return order.toResponseDto();
    }

    @Override
    public Page<OrdersResponseDto> searchOrder(String memberUuid, Status status, LocalDateTime starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {

        log.info("Searching orders for member UUID:{}", memberUuid);

        return ordersRepository.searchOrders(memberUuid, status, starDate, endDate, minAmount, maxAmount, sortField, pageable);
    }

    @Override
    @Transactional
    public OrdersResponseDto cancelOrder(String memberUuid, String orderUuid) {

        log.info("Updating order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders orders = ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, orderUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or order UUID "));

        matchMemberUuid(memberUuid, orders);

        if (orders.getStatus() != Status.PENDING){
            throw new IllegalArgumentException("Order is already confirmed");
        }

        orders.ordersStatus(Status.CANCELLED);

        log.info("Updated order:{}", orders);

        return orders.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteOrder(String memberUuid, String orderUuid) {

        log.info("Deleting order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders orders = ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, orderUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or order UUID "));

        if (orders.getStatus() != Status.CANCELLED){
            throw new IllegalArgumentException("Can not deleted except fro canceled order");
        }

        matchMemberUuid(memberUuid, orders);

        ordersRepository.delete(orders);
    }

    private void matchMemberUuid(String memberUuid, Orders orders) {
        if (!Objects.equals(orders.getMember().getMemberUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUID dose not match the order owner");
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

    public BigDecimal calculateTotalAmount(List<OrderItemRequestDto> orderItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDto item : orderItems) {

            Product product = productRepository.findByProductUuid(item.getProductUuid())
                    .orElseThrow(() -> new IllegalArgumentException("Not found product ID"));

            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }
}



