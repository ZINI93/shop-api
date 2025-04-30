package com.zinikai.shop.domain.product.repository;

import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findByProductUuid(String productUuid);

    Optional<Product> findByMemberMemberUuidAndProductUuid(String ownerUuid, String productUuid);

    @EntityGraph(attributePaths = {"member"})
    List<Product> findAllByProductUuidIn(List<String> productUUid);
}
