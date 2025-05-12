package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {


    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    OrdersRepository ordersRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    OrderItemServiceImpl orderItemService;

    OrderItem orderItem;
    OrderItemRequestDto orderItemRequestDto;

    Member member;

    Orders orders;

    Product product;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setOrderId(Orders Orders, Long id) throws Exception {
        Field field = Orders.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(Orders, id);
    }

    private void setProductId(Product product, Long id) throws Exception {
        Field field = product.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(product, id);
    }


    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        orders = Orders.builder().orderUuid(UUID.randomUUID().toString()).build();
        setOrderId(orders, 1L);

        product = Product.builder().build();
        setProductId(product, 1L);


        orderItemRequestDto = new OrderItemRequestDto(UUID.randomUUID().toString(), 100);

        orderItem = new OrderItem(
                orders,
                product,
                orderItemRequestDto.getQuantity(),
                new BigDecimal("2000.00"),
                member.getMemberUuid(),
                UUID.randomUUID().toString(),
                product.getMember().getMemberUuid());


    }

    @Test
    void createOrderItem() {
        //given
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        //when
        orderItemService.createAndSaveOrderItem(member, orderItemRequestDto, orders, product);

        //then
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));

    }

    @Test
    void getAllOrderItem() {
        //given


        PageRequest pageable = PageRequest.of(0, 10);
        List<OrderItem> mockOrderItems = List.of(orderItem);
        Page<OrderItem> mockOrderItem = new PageImpl<>(mockOrderItems, pageable, mockOrderItems.size());

        when(orderItemRepository.findAllByOwnerUuidOrderByCreatedAtDesc(orderItem.getOwnerUuid(), pageable)).thenReturn(mockOrderItem);


        //when
        Page<OrderItemResponseDto> result = orderItemService.getOrderItems(orderItem.getOwnerUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(mockOrderItems.size(), result.getTotalElements());
        verify(orderItemRepository, times(1)).findAllByOwnerUuidOrderByCreatedAtDesc(orderItem.getOwnerUuid(), pageable);

    }
}
