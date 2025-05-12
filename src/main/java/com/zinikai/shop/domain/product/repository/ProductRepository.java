package com.zinikai.shop.domain.product.repository;

import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findByProductUuid(String productUuid);

    @Query("select p from Product p join fetch p.member where p.member.memberUuid = :memberUuid and p.productUuid = :productUuid")
    Optional<Product> findByMemberMemberUuidAndProductUuid(@Param("memberUuid") String memberUuid, @Param("productUuid") String productUuid);

    @EntityGraph(attributePaths = {"member"})
    List<Product> findAllByProductUuidIn(List<String> productUUid);
}
