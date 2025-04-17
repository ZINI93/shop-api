package com.zinikai.shop.domain.category.repository;

import com.zinikai.shop.domain.category.entity.Category;
import org.hibernate.annotations.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByOwnerUuidAndCategoryUuid(String memberUuid, String categoryUuid);

  Optional<Category> findByParentId(Long parentId);

  Optional<Category> findByParentCategoryUuid(String categoryUuid);
  Optional<Category> findByCategoryUuid(String categoryUuid);

  @Query("select MAX(c.sortOrder) from Category c where c.parent.categoryUuid = :categoryUuid")
  Integer findMaxSortOrderByParent(@Param("categoryUuid") String categoryUuid);


  Page<Category> findAllByOwnerUuid(String memberUuid, Pageable pageable);

  Page<Category> findByParentIsNullOrderBySortOrderAsc(Pageable pageable);

  }

