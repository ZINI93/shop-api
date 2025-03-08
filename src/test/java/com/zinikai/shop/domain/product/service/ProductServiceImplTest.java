package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private MemberRepository memberRepository;
    @InjectMocks private ProductServiceImpl productService;

    ProductRequestDto requestDto;
    Product product;
    Member member;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }


    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);


        product = new Product(
                "kakao",
                new BigDecimal(1000),
                "美味しいカカオ",
                10,
                UUID.randomUUID().toString(),
                member.getMemberUuid()
        );


        requestDto = new ProductRequestDto(
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock()
        );

    }

    @Test
    @DisplayName("商品登録")
    void testCreateProduct() {
        //given

        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(member));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        ProductResponseDto savedProduct = productService.createProduct(member.getId(),requestDto);

        //then
        assertNotNull(savedProduct);
        assertEquals("kakao", savedProduct.getName());
        assertEquals(new BigDecimal(1000), savedProduct.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }

//    @Test
//    void findByIdTest() {
//        //given
//
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<ProductResponseDto> mockProducts = List.of(product.toResponseDto());
//        Page<ProductResponseDto> productResponseDtos = new PageImpl<>(mockProducts, pageable, mockProducts.size());
//
//
//        when(productRepository.searchProduct(member.getMemberUuid(),)).thenReturn(Optional.ofNullable(mockProducts));
//
//        //when
//        ProductResponseDto result = productService.getProductById(1L);
//
//        assertNotNull(result);
//        assertEquals(testProduct.getName(), result.getName());
//        verify(productRepository, times(1)).findById(1L);
//    }


    @Test
    @DisplayName("アップデート")
    void updateProduct() {
        //given

        ProductUpdateDto updateProduct = new ProductUpdateDto("momoka", new BigDecimal(3000), "masita kakao", 20);

        when(productRepository.findByOwnerUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid())).thenReturn(Optional.of(product));

        //when
        ProductResponseDto updatedProduct = productService.updateProduct(member.getMemberUuid(),product.getProductUuid(),updateProduct);


        //then
        assertNotNull(updatedProduct);
        assertEquals("momoka", updatedProduct.getName());
        assertEquals( new BigDecimal(3000), updatedProduct.getPrice());
        verify(productRepository, times(1)).findByOwnerUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid());

    }

    @Test
    @DisplayName("商品を削除")
    void deleteProduct() {
        //given
        when(productRepository.findByOwnerUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid())).thenReturn(Optional.of(product));

        //when
        productService.deleteProduct(member.getMemberUuid(),product.getProductUuid());

        //then
        verify(productRepository, times(1)).findByOwnerUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid());

    }
}