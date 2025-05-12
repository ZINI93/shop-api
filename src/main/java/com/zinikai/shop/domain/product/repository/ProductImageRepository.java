package com.zinikai.shop.domain.product.repository;

import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
import com.zinikai.shop.domain.product.dto.ProductWithImagesDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    Optional<ProductImage> findByOwnerUuidAndProductImageUuid(String ownerUuid,String productUuid);

    @Query("SELECT pi FROM Product p, ProductImage pi " +
            "WHERE p.id = pi.product.id " +
            "AND p.productUuid = :productUuid " +
            "AND pi.productImageUuid = :productImageUuid")
    List<ProductImage> findProductImagesByUuids(
            @Param("productUuid") String productUuid,
            @Param("productImageUuid") String productImageUuid
    );

    @Query("select pi from ProductImage pi join fetch pi.product p where p.productUuid = :productUuid")
    List<ProductImage> findAllByProductProductUuid(@Param("productUuid") String productUuid);

    Optional<ProductImage> findByProductProductUuid(String productUuid);
    Page<ProductImageResponseDto> findAllByOwnerUuid(String ownerUuid, Pageable pageable);
    int countByProduct(Product product);

    @Query("select pi from ProductImage pi left join fetch pi.product p where p.productUuid = :productUuid")
    List<ProductImage> findProductImagesByProductUuid(@Param("productUuid")String productUuid);

    ProductImageResponseDto deleteByProduct(Product product);

}
