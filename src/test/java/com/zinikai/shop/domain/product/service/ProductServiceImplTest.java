package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.entity.ProductCategory;
import com.zinikai.shop.domain.category.repository.CategoryRepository;
import com.zinikai.shop.domain.category.repository.ProductCategoryRepository;
import com.zinikai.shop.domain.category.service.ProductCategoryServiceImpl;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductCategoryServiceImpl productCategoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    ProductRequestDto requestDto;
    Product product;
    Member member;
    ProductImage productImage;
    Category category;
    ProductCategory productCategory;
    List<ProductImage> images;


    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    @BeforeEach
    void setup() throws Exception {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        category = Category.builder().categoryUuid(UUID.randomUUID().toString()).build();

        productImage = ProductImage.builder().imageUrl("www.image.com").productImageUuid(UUID.randomUUID().toString()).build();

        productCategory = new ProductCategory(category, product, UUID.randomUUID().toString());
        images = Arrays.asList(new ProductImage(product, "www.image.com", member.getMemberUuid(), UUID.randomUUID().toString()),
                new ProductImage(product, "www.image2.com", member.getMemberUuid(), UUID.randomUUID().toString()));

        List<ProductImageResponseDto> productImageResponseDtoStream = images.stream().map(ProductImage::toResponse).collect(Collectors.toList());


        requestDto = new ProductRequestDto(
                "自転車",
                new BigDecimal("2000.00"),
                "キラキラ自転車",
                10,
                ProductCondition.NEW,
                "zini-shop",
                productImageResponseDtoStream,
                category.getCategoryUuid()
        );

        product = new Product(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getDescription(),
                requestDto.getStock(),
                ProductStatus.ON_SALE,
                requestDto.getProductCondition(),
                requestDto.getProductMaker(),
                UUID.randomUUID().toString(),
                member
        );
    }

    @Test
    void createProcessProduct() {

        //given
        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(categoryRepository.findByCategoryUuid(requestDto.getCategoryUuid())).thenReturn(Optional.ofNullable(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        ProductResponseDto result = productService.createProductProcess(product.getMember().getMemberUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(product.getProductCondition(),result.getProductCondition());
        assertEquals(product.getDescription(),result.getDescription());

        verify(memberRepository,times(1)).findByMemberUuid(member.getMemberUuid());
        verify(categoryRepository,times(1)).findByCategoryUuid(category.getCategoryUuid());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void findByIdTest() {
        //given

        String sortField = "createAt";

        PageRequest pageable = PageRequest.of(0, 10);
        List<ProductResponseDto> mockProducts = List.of(product.toResponseDto(null));
        PageImpl<ProductResponseDto> mockProduct = new PageImpl<>(mockProducts, pageable, mockProducts.size());

        when(productRepository.searchProduct(
               eq(product.getMember().getMemberUuid()),
                contains("自"),
                any(),
                any(),
                eq(sortField),
                eq(pageable)
        )).thenReturn(mockProduct);

        //when
        Page<ProductResponseDto> result = productService.searchProducts(product.getMember().getMemberUuid(), product.getName(), new BigDecimal(100.00), new BigDecimal(2000.00), sortField, pageable);

        assertNotNull(result);

        verify(productRepository, times(1)).searchProduct( eq(product.getMember().getMemberUuid()),
                contains("自"),
                any(),
                any(),
                eq(sortField),
                eq(pageable));
    }



    @Test
    @DisplayName("アップデート")
    void updateProduct() {

        //given

        ProductUpdateDto productUpdateDto = new ProductUpdateDto("포테이토칩", new BigDecimal(1000.00), "맛있는 포테이토칩", 100, null);
        when(productRepository.findByMemberMemberUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid())).thenReturn(Optional.of(product));

        //when
        ProductResponseDto result = productService.updateProduct(product.getMember().getMemberUuid(), product.getProductUuid(), productUpdateDto);


        //then
        assertNotNull(result);
        assertEquals(productUpdateDto.getName(), result.getName());
        assertEquals(productUpdateDto.getPrice(), result.getPrice());

        verify(productRepository, times(1)).findByMemberMemberUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid());
    }

    @Test
    @DisplayName("商品を削除")
    void deleteProduct() {
        //given
        when(productRepository.findByMemberMemberUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid())).thenReturn(Optional.of(product));

        //when
        productService.deleteProduct(member.getMemberUuid(),product.getProductUuid());

        //then
        verify(productRepository, times(1)).findByMemberMemberUuidAndProductUuid(member.getMemberUuid(),product.getProductUuid());

    }
}