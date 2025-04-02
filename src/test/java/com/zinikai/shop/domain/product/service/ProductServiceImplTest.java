package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductCondition;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.entity.ProductStatus;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProductImageRepository productImageRepository;
    @InjectMocks private ProductServiceImpl productService;

    ProductRequestDto requestDto;
    Product product;
    Member member;
    ProductImage productImage;


    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        product  = Product.builder().productUuid(UUID.randomUUID().toString()).build();

        productImage = ProductImage.builder().imageUrl("www.image.com").productImageUuid(UUID.randomUUID().toString()).build();

        requestDto = new ProductRequestDto(
                "自転車",
                new BigDecimal("2000.00"),
                "キラキラ自転車",
                10,
                ProductCondition.NEW,
                "zini-shop",
                Collections.emptyList()
        );

        product = new Product(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getDescription(),
                requestDto.getStock(),
                ProductStatus.ON_SALE,
                requestDto.getProductCondition(),
                requestDto.getProductMaker(),
                product.getProductUuid(),
                member.getMemberUuid()
        );


    }

    @Test
    @DisplayName("商品登録")
    void testCreateProduct() {
        //given
        List<ProductImage> images = Arrays.asList(new ProductImage(product, "www.zini.com", member.getMemberUuid(),UUID.randomUUID().toString()));


        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productImageRepository.saveAll(anyList())).thenReturn(images);

        //when
        ProductResponseDto savedProduct = productService.createProduct(member.getMemberUuid(),requestDto);

        //then
        assertNotNull(savedProduct);
        assertEquals(requestDto.getName(), savedProduct.getName());
        assertEquals(requestDto.getPrice(), savedProduct.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productImageRepository, times(1)).saveAll(anyList());
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