package com.zinikai.shop.domain.category.service;

import com.zinikai.shop.domain.category.dto.CategoryRequestDto;
import com.zinikai.shop.domain.category.dto.CategoryResponseDto;
import com.zinikai.shop.domain.category.dto.CategoryUpdateDto;
import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.repository.CategoryRepository;
import com.zinikai.shop.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock CategoryRepository categoryRepository;

    @InjectMocks CategoryServiceImpl categoryService;


    CategoryRequestDto categoryRequestDto;

    Category category;

    Member member;

    @BeforeEach
    void setup(){

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        category = category.builder().parent(null).build();



        categoryRequestDto = CategoryRequestDto.builder()
                .name("パソコン")
                .slug("Computer")
                .parentUuid(null)
                .sortOrder(1)
                .build();

        category = new Category(
                UUID.randomUUID().toString(),
                member.getMemberUuid(),
                categoryRequestDto.getName(),
                categoryRequestDto.getSlug(),
                category.getParent(),
                categoryRequestDto.getSortOrder());

    }

    @Test
    void createCategory() {
        //given

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        //when
        CategoryResponseDto result = categoryService.createCategory(member.getMemberUuid(), categoryRequestDto);


        //then
        assertNotNull(result);
        assertEquals(categoryRequestDto.getName(),result.getName());

        verify(categoryRepository, times(1)).save(any(Category.class));

    }

    @Test
    void getCategoryInfo() {

        //given
        when(categoryRepository.findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid())).thenReturn(Optional.ofNullable(category));



        //when
        CategoryResponseDto result = categoryService.getCategoryInfo(category.getOwnerUuid(), category.getCategoryUuid());

        //then
        assertNotNull(result);
        assertEquals(category.getCategoryUuid(), result.getCategoryUuid());
        assertEquals(category.getName(), result.getName());

        verify(categoryRepository, times(1)).findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid());

    }

    @Test
    void getCategoryList() {

        //given
        PageRequest pageable = PageRequest.of(10, 1);

        List<Category> mockCategory = List.of(category);

        PageImpl<Category> mockPage = new PageImpl<>(mockCategory, pageable, mockCategory.size());

        when(categoryRepository.findAllByOwnerUuid(member.getMemberUuid(),pageable)).thenReturn(mockPage);


        //when
        Page<CategoryResponseDto> result = categoryService.getCategoryList(member.getMemberUuid(), pageable);


        //then
        assertNotNull(result);
        assertEquals(mockPage.getSize(), result.getSize());

        verify(categoryRepository,times(1)).findAllByOwnerUuid(member.getMemberUuid(),pageable);

    }

    @Test
    void getParentCategoryList() {

        //given

        PageRequest pageable = PageRequest.of(10, 1);

        List<Category> mockCategory = List.of(category);

        PageImpl<Category> mockPage = new PageImpl<>(mockCategory, pageable, mockCategory.size());

        when(categoryRepository.findByParentIsNullOrderBySortOrderAsc(pageable)).thenReturn(mockPage);


        //when
        Page<CategoryResponseDto> result = categoryService.getParentCategoryList(pageable);


        //then
        assertNotNull(result);
        assertEquals(mockCategory.size() , result.getSize());

        verify(categoryRepository, times(1)).findByParentIsNullOrderBySortOrderAsc(pageable);






    }

    @Test
    void updateCategory() {
        //given
        when(categoryRepository.findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid())).thenReturn(Optional.ofNullable(category));

        CategoryUpdateDto categoryUpdateDto = new CategoryUpdateDto("自転車", "Cycle", category.getParent(), 2);

        //when
        CategoryResponseDto result = categoryService.updateCategory(category.getOwnerUuid(), category.getCategoryUuid(), categoryUpdateDto);

        //then
        assertNotNull(result);
        assertEquals(categoryUpdateDto.getName(),result.getName());
        assertEquals(categoryUpdateDto.getSlug(), result.getSlug());

        verify(categoryRepository, times(1)).findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid());

    }

    @Test
    void deleteCategory() {
        //given
        when(categoryRepository.findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid())).thenReturn(Optional.ofNullable(category));

        //when
        categoryService.deleteCategory(category.getOwnerUuid(), category.getCategoryUuid());

        //then
        verify(categoryRepository, times(1)).findByOwnerUuidAndCategoryUuid(category.getOwnerUuid(),category.getCategoryUuid());


    }
}