package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductImageRequestDto;
import com.zinikai.shop.domain.product.dto.ProductWithImagesDto;
import com.zinikai.shop.domain.product.dto.ProductImageUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductImageServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductImageRepository productImageRepository;

    @InjectMocks
    ProductImageServiceImpl productImageService;

    Member member;
    Product product;
    ProductImageRequestDto requestDto;
    ProductImage productImage;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    private void setProductId(Product product, Long id) throws Exception {
        Field field = product.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(product, id);
    }

    @BeforeEach
    void setUp() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        product = Product.builder().build();
        setProductId(product, 1L);


        requestDto = new ProductImageRequestDto(
                1L,
                "wwww.zini.com",
                member.getMemberUuid());

        productImage = new ProductImage(product,
                requestDto.getImageUrl(),
                member.getMemberUuid(),
                UUID.randomUUID().toString());


    }

    @Test
    void getImagesByMember() {

        //given
        PageRequest pageable = PageRequest.of(0, 10);
        List<ProductWithImagesDto> mockProductImages = List.of(productImage.toResponse());
        Page<ProductWithImagesDto> mockImagePage = new PageImpl<>(mockProductImages, pageable, mockProductImages.size());

        when(productImageRepository.findAllByOwnerUuid(member.getMemberUuid(), pageable)).thenReturn(mockImagePage);
        //when

        Page<ProductWithImagesDto> result = productImageService.getImagesByMember(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(mockProductImages.size(), result.getTotalElements());

        verify(productImageRepository, times(1)).findAllByOwnerUuid(member.getMemberUuid(), pageable);
    }

    @Test
    void updateProductImage() {

        //given
        ProductImageUpdateDto updateDto = new ProductImageUpdateDto("www.kakao.com");

        when(productImageRepository.findByOwnerUuidAndProductImageUuid(member.getMemberUuid(), product.getProductUuid())).thenReturn(Optional.ofNullable(productImage));

        //when
        ProductWithImagesDto result = productImageService.updateProductImage(member.getMemberUuid(), product.getProductUuid(), updateDto);


        //then
        assertNotNull(result);
        assertEquals(updateDto.getImageUrl(), result.getImageUrl());

        verify(productImageRepository, times(1)).findByOwnerUuidAndProductImageUuid(member.getMemberUuid(), product.getProductUuid());
    }

    @Test
    void deleteProductImage() {

        //given
        when(productImageRepository.findByOwnerUuidAndProductImageUuid(member.getMemberUuid(), product.getProductUuid())).thenReturn(Optional.ofNullable(productImage));

        //when
        productImageService.deleteProductImage(member.getMemberUuid(), product.getProductUuid());

        //then
        verify(productImageRepository, times(1)).findByOwnerUuidAndProductImageUuid(member.getMemberUuid(), product.getProductUuid());

    }
}