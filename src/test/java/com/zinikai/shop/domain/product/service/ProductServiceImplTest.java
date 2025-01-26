package com.zinikai.shop.domain.product.service;

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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private  ProductRepository productRepository;

    @InjectMocks private ProductServiceImpl productService;

    ProductRequestDto requestDto;

    @BeforeEach
    void setup(){
        requestDto = new ProductRequestDto(
                "kakao"
                , new BigDecimal(2000),
                "美味しいカカオ",
                10
        );
    }
    @Test
    @DisplayName("商品登録")
    void testCreateProduct(){
        //given
        Product testProduct = new Product(
                1L,
                "kakao",
                new BigDecimal(2000),
                "美味しいカカオ",
                10
        );
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        //when
        ProductResponseDto savedProduct = productService.createProduct(requestDto);

        //then
        assertNotNull(savedProduct);
        assertEquals("kakao", savedProduct.getName());
        assertEquals(new BigDecimal(2000), savedProduct.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("IDで探す")
    void findByIdTest(){
        //given
        Product testProduct = new Product(
                1L,
                "kakao",
                new BigDecimal(2000),
                "美味しいカカオ",
                10
        );
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(testProduct));

        //when
        ProductResponseDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(testProduct.getName(),result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    //전체 상품, 이름, 가격범위검색, 페이징
//    @Test
//    @DisplayName("商品　ー　名前、価格범위검색")
//

    @Test
    @DisplayName("アップデート")
    void updateProduct(){
        //given
        Product product = new Product(1L, "kakao", new BigDecimal(2000), "美味しいカカオ", 20);

        ProductUpdateDto productUpdateDto = new ProductUpdateDto(
                "cocoa",
                new BigDecimal(1000),
                "美味しいココア",
                10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        ProductResponseDto updatedProduct = productService.updateProduct(1L, productUpdateDto);


        //then
        assertNotNull(updatedProduct);
        assertEquals(productUpdateDto.getName(),updatedProduct.getName());
        assertEquals(productUpdateDto.getPrice(), updatedProduct.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    @DisplayName("商品を削除")
    void deleteProduct(){
        //given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);
        //when
        productService.deleteProduct(1L);

        //then
        verify(productRepository, times(1)).deleteById(1L);

    }
}