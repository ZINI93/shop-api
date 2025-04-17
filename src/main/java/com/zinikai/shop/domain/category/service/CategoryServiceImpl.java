package com.zinikai.shop.domain.category.service;

import com.zinikai.shop.domain.category.dto.CategoryRequestDto;
import com.zinikai.shop.domain.category.dto.CategoryResponseDto;
import com.zinikai.shop.domain.category.dto.CategoryUpdateDto;
import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override @Transactional
    public CategoryResponseDto createCategory(String memberUuid, CategoryRequestDto requestDto) {

        log.info("Creating category for member UUID:{}", memberUuid);

        Category parent = null;
        if (requestDto.getParentUuid() != null){
            parent = categoryRepository.findByCategoryUuid(requestDto.getParentUuid())
                    .orElseThrow(()-> new IllegalArgumentException("Not found Category UUID"));
        }

        Integer sortOrder = requestDto.getSortOrder();
        if (sortOrder == null){
            sortOrder = categoryRepository.findMaxSortOrderByParent(requestDto.getParentUuid());
            sortOrder = (sortOrder == null) ? 1 : sortOrder + 1;
        }

        Category savedCategory = Category.builder()
                .ownerUuid(memberUuid)
                .name(requestDto.getName())
                .slug(requestDto.getSlug())
                .parent(parent)
                .sortOrder(sortOrder)
                .build();

        log.info("Created category UUID:{}", savedCategory.getCategoryUuid());

        return categoryRepository.save(savedCategory).toResponseDto();
    }

    @Override
    public CategoryResponseDto getCategoryInfo(String memberUuid, String categoryUuid) {

        log.info("Searching category for member UUID:{} or category UUID:{}", memberUuid, categoryUuid);

        Category category = matchMemberUuidAndCategoryUuid(memberUuid, categoryUuid);

        return category.toResponseDto();
    }

    @Override
    public Page<CategoryResponseDto> getCategoryList(String memberUuid, Pageable pageable) {
        Page<Category> allByOwnerUuid = categoryRepository.findAllByOwnerUuid(memberUuid, pageable);

        return allByOwnerUuid.map(Category::toResponseDto);
    }

    @Override
    public Page<CategoryResponseDto> getParentCategoryList(Pageable pageable) {

        Page<Category> parentCategories = categoryRepository.findByParentIsNullOrderBySortOrderAsc(pageable);

        return parentCategories.map(Category::toResponseDto);
    }


    @Override @Transactional
    public CategoryResponseDto updateCategory(String memberUuid, String categoryUuid, CategoryUpdateDto updateDto) {
        Category category = matchMemberUuidAndCategoryUuid(memberUuid, categoryUuid);

        Category updatedCategory = Category.builder()
                .name(updateDto.getName())
                .slug(updateDto.getSlug())
                .parent(updateDto.getParent())
                .sortOrder(updateDto.getSortOrder())
                .build();

        return updatedCategory.toResponseDto();
    }

    @Override @Transactional
    public void deleteCategory(String memberUuid, String categoryUuid) {

        Category category = matchMemberUuidAndCategoryUuid(memberUuid, categoryUuid);

        categoryRepository.delete(category);
    }

    private Category matchMemberUuidAndCategoryUuid(String memberUuid, String categoryUuid) {
        return categoryRepository.findByOwnerUuidAndCategoryUuid(memberUuid, categoryUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID or category UUid"));
    }
}
