package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.CouponUsage;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.repository.UserCouponRepository;
import com.zinikai.shop.domain.coupon.service.CouponUsageServiceImpl;
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
import com.zinikai.shop.domain.payment.service.PaymentService;
import com.zinikai.shop.domain.payment.service.PaymentServiceImpl;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock OrdersRepository ordersRepository;
    @Mock AddressRepository addressRepository;
    @Mock ProductRepository productRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock UserCouponRepository userCouponRepository;
    @Mock OrderItemServiceImpl orderItemService;
    @Mock PaymentServiceImpl paymentService;
    @Mock CouponUsageServiceImpl couponUsageService;
    @InjectMocks OrdersServiceImpl orderService;

    OrdersRequestDto ordersRequest;

    Orders orders;
    Address address;
    Member member;
    Product product;
    OrderItem orderItem;
    UserCoupon userCoupon;
    List<OrderItemRequestDto> orderItems;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setOrdersId(Orders orders, Long id) throws Exception {
        Field field = orders.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(orders, id);
    }

    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        address = Address.builder().member(Member.builder().memberUuid(member.getMemberUuid()).build()).build();
        userCoupon = UserCoupon.builder().userCouponUuid(UUID.randomUUID().toString()).build();

        product = Product.builder().member(Member.builder().memberUuid(member.getMemberUuid()).build()).price(new BigDecimal(1000)).productUuid(UUID.randomUUID().toString()).build();

        orderItems = new ArrayList<>();
        OrderItemRequestDto orderItemRequestDto1 = new OrderItemRequestDto(product.getProductUuid(), 100);
        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(product.getProductUuid(), 100);

        orderItems.add(orderItemRequestDto1);
        orderItems.add(orderItemRequestDto2);

        orders = new Orders(
                member,
                new BigDecimal(10),
                Status.ORDER_PENDING,
                "PayPay",
                UUID.randomUUID().toString(),
                address,
                product.getMember().getMemberUuid(),
                new BigDecimal(100)
        );

        setOrdersId(orders, 1L);

        ordersRequest = new OrdersRequestDto(
                orders.getPaymentMethod(),
                userCoupon.getUserCouponUuid(),
                orderItems

        );
    }

    @Test
    @DisplayName("オーダーを作成")
    void createOrder() {

        //given


        //when
        Orders result = orderService.createOrder(member,orders.getTotalAmount(),ordersRequest,product.getMember().getMemberUuid(),address,orders.getDiscountAmount());

        //then
        assertNotNull(result);
        assertEquals(1L, orders.getId());
        assertEquals("PayPay", orders.getPaymentMethod());


    }

    @Test
    void orderProcess(){

        //given
        List<String> productIds = orderItems.stream().map(OrderItemRequestDto::getProductUuid)
                .collect(Collectors.toList());


        when(memberRepository.findByMemberUuid(orders.getMember().getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(addressRepository.findByMemberMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(address));
        when(userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(member.getMemberUuid(), userCoupon.getUserCouponUuid())).thenReturn(Optional.ofNullable(userCoupon));
        when(productRepository.findAllByProductUuidIn(productIds)).thenReturn(List.of(product));
        when(productRepository.findByProductUuid(orderItems.get(0).getProductUuid())).thenReturn(Optional.ofNullable(product));
        when(ordersRepository.save(any(Orders.class))).thenReturn(orders);

        //when
        OrdersResponseDto result = orderService.orderProcess(member.getMemberUuid(), ordersRequest);


        //then

        assertNotNull(result);
        assertEquals(ordersRequest.getPaymentMethod(),result.getPaymentMethod());

        verify(ordersRepository,times(1)).save(any(Orders.class));
    }


    @Test
    @DisplayName("オーダーをサーチ")
    void TestSearch() {
        //given
        String sortFiled = "createAt";

        PageRequest pageable = PageRequest.of(0, 10);
        List<OrdersResponseDto> mockOrders = List.of(orders.toResponseDto());
        Page<OrdersResponseDto> mockOrder = new PageImpl<>(mockOrders, pageable, mockOrders.size());

        // 偽物データをRETURN設定
        Page<OrdersResponseDto> mockOrderpage = mock(Page.class);
        when(ordersRepository.searchOrders(
                eq(member.getMemberUuid()),
                eq(orders.getStatus()),
                any(),
                any(),
                any(),
                any(),
                eq(sortFiled),
                eq(pageable)))
                .thenReturn(mockOrder);

        //when
        Page<OrdersResponseDto> result = orderService.searchOrder(
                member.getMemberUuid(),
                orders.getStatus(),
                orders.getCreatedAt(),
                orders.getUpdatedAt(),
                orders.getTotalAmount(),
                orders.getTotalAmount(),
                sortFiled,
                pageable);

        //then
        assertNotNull(result);
        assertEquals(mockOrder, result);
        verify(ordersRepository, times(1)).searchOrders(
                eq(member.getMemberUuid()),
                eq(orders.getStatus()),
                any(),
                any(),
                any(),
                any(),
                eq(sortFiled),
                eq(pageable));

    }

    @Test
    @DisplayName("オーダーをアップデート")
    void TestOrderUpdate() {
        //given

        OrdersUpdateDto updateOrder = OrdersUpdateDto.builder()
                .paymentMethod(ordersRequest.getPaymentMethod())
                .status(Status.ORDER_COMPLETED)
                .build();


        when(ordersRepository.findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid())).thenReturn(Optional.ofNullable(orders));

        //when
        OrdersResponseDto result = orderService.cancelOrder(member.getMemberUuid(),orders.getOrderUuid());

        //then
        assertNotNull(result);
        assertEquals(orders.getTotalAmount(), result.getTotalAmount());
        assertEquals(orders.getPaymentMethod(), result.getPaymentMethod());

        verify(ordersRepository, times(1)).findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),orders.getOrderUuid());
    }

    @Test
    @DisplayName("オーダーを削除")
    void TestDeleteOrder() {
        //given

        Orders testOrder = new Orders(member, new BigDecimal(1000), Status.ORDER_CANCELLED, "a", UUID.randomUUID().toString(), address, orders.getSellerUuid(), new BigDecimal(1000));

        when(ordersRepository.findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),testOrder.getOrderUuid())).thenReturn(Optional.ofNullable(testOrder));



        System.out.println("check" + testOrder.getStatus());
        //when
        orderService.deleteOrder(member.getMemberUuid(),testOrder.getOrderUuid());

        //then
        verify(ordersRepository, times(1)).findByMemberMemberUuidAndOrderUuid(member.getMemberUuid(),testOrder.getOrderUuid());
    }

    @Test
    @Transactional
    public void testBulkUpdate() {
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        int result = ordersRepository.bulkCancelExpiredOrders(
                Status.ORDER_PENDING,
                Status.ORDER_CANCELLED,
                time
        );
        Assertions.assertThat(result).isGreaterThan(0);
    }

}
