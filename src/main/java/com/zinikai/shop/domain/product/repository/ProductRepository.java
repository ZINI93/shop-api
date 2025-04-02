package com.zinikai.shop.domain.product.repository;

import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findByProductUuid(String productUuid);

    Optional<Product> findByOwnerUuid(String ownerUuid);

    Optional<Product> findByOwnerUuidAndProductUuid(String ownerUuid, String productUuid);

    List<Product> findAllByProductUuidIn(List<String> productUUid);
}
