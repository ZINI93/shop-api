package com.zinikai.shop.domain.delivery.service;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.delivery.dto.DeliveryRequestDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryResponseDto;
import com.zinikai.shop.domain.delivery.dto.DeliveryUpdateDto;
import com.zinikai.shop.domain.delivery.entity.Carrier;
import com.zinikai.shop.domain.delivery.entity.Delivery;
import com.zinikai.shop.domain.delivery.entity.DeliveryStatus;
import com.zinikai.shop.domain.delivery.repository.DeliveryRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    OrdersRepository ordersRepository;

    @Mock
    DeliveryRepository deliveryRepository;

    @InjectMocks
    DeliveryServiceImpl deliveryService;

    Member member;
    Orders orders;
    Delivery delivery;
    Address address;

    DeliveryRequestDto requestDto;

    private void setOrdersId(Orders orders, Long id) throws Exception {
        Field field = orders.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(orders, id);
    }


    @BeforeEach
    void setUp() throws Exception {

        address = Address.builder().addressUuid(UUID.randomUUID().toString()).build();

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();

        orders = Orders.builder().orderUuid(UUID.randomUUID().toString()).address(address).member(Member.builder().memberUuid(UUID.randomUUID().toString()).build()).status(Status.COMPLETED).build();
        setOrdersId(orders, 1L);

        requestDto = new DeliveryRequestDto(
                orders.getOrderUuid(),
                "1234-1234",
                Carrier.JAPANPOST
        );

        delivery = new Delivery(
                orders,
                orders.getAddress(),
                DeliveryStatus.PENDING,
                requestDto.getTrackingNumber(),
                requestDto.getCarrier(),
                member.getMemberUuid(),
                orders.getMember().getMemberUuid(),
                UUID.randomUUID().toString()
        );

    }


    @DisplayName("発送作成")
    @Test
    void createDeliveryTest() {

        //given
        when(ordersRepository.findByOrderUuid(orders.getOrderUuid())).thenReturn(Optional.ofNullable(orders));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        //when
        DeliveryResponseDto result = deliveryService.createDelivery(member.getMemberUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(orders.getAddress().getAddressUuid(), result.getAddressUuid());
        assertEquals(DeliveryStatus.PENDING, result.getDeliveryStatus());

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @DisplayName("販売者が発送情報を確認")
    @Test
    void getSellerDeliveryInfoTest() {

        //given
        when(deliveryRepository.findByMemberUuidAndDeliveryUuid(member.getMemberUuid(), delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));

        //when
        DeliveryResponseDto result = deliveryService.getDeliveryInfo(member.getMemberUuid(), delivery.getDeliveryUuid());

        //then
        assertNotNull(result);
        assertEquals(orders.getOrderUuid(), result.getOrderUuid());


        verify(deliveryRepository, times(1)).findByMemberUuidAndDeliveryUuid(member.getMemberUuid(), delivery.getDeliveryUuid());
    }

    @DisplayName("購入者が発送情報を確認")
    @Test
    void getBuyerDeliveryInfoTest() {

        //given

        when(deliveryRepository.findByMemberUuidAndDeliveryUuid(orders.getMember().getMemberUuid(), delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));

        //when
        DeliveryResponseDto result = deliveryService.getDeliveryInfo(orders.getMember().getMemberUuid(), delivery.getDeliveryUuid());
        System.out.println("member.getMemberUuid() = " + member.getMemberUuid());
        System.out.println("orders.getMember().getMemberUuid() = " + orders.getMember().getMemberUuid());
        System.out.println("result = " + result.getBuyerUuid());

        //then
        assertNotNull(result);
        assertEquals(orders.getOrderUuid(), result.getOrderUuid());
        assertEquals(orders.getMember().getMemberUuid(), result.getBuyerUuid());

        verify(deliveryRepository, times(1)).findByMemberUuidAndDeliveryUuid(orders.getMember().getMemberUuid(), delivery.getDeliveryUuid());
    }

    @DisplayName("発送をアップデート")
    @Test
    void updateDelivery() {
        //given
        when(deliveryRepository.findByMemberUuidAndDeliveryUuid(member.getMemberUuid(), delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));

        //when

        DeliveryUpdateDto updateDto = DeliveryUpdateDto.builder().carrier(Carrier.YAMATO).trackingNumber("1234-1234").build();
        delivery.updateInfo(updateDto);
        deliveryService.updateDelivery(member.getMemberUuid(), delivery.getDeliveryUuid(), updateDto);

        //then
        assertNotNull(delivery);
        assertEquals(DeliveryStatus.PENDING, delivery.getDeliveryStatus());
        assertEquals(Carrier.YAMATO,updateDto.getCarrier());

        verify(deliveryRepository, times(1)).findByMemberUuidAndDeliveryUuid(orders.getMember().getMemberUuid(), delivery.getDeliveryUuid());
    }


}