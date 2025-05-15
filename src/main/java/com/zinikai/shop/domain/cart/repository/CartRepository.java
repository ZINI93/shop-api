package com.zinikai.shop.domain.cart.repository;

import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Page<CartResponseDto> findAllByMemberMemberUuid(String memberUuid, Pageable pageable);
    Optional<Cart> findByMemberMemberUuidAndCartUuid(String ownerUuid, String cartUuid);
    List<Cart> findAllByMemberMemberUuid(String memberUuid);
    Optional<Cart> findByMemberMemberUuidAndProductId(String memberUuid, Long productId);
    @Query("select c from Cart c where c.member.memberUuid = :memberUuid and c.cartUuid In :cartUuids")
    List<Cart> findAllByMemberUuidAndCartUuids(@Param("memberUuid") String memberUuid, @Param("cartUuids") List<String> cartUuids);
    int countByMember(Member member);


    @Query("select COUNT(c) from Cart c where c.product = :product and c.member = :member")
    int countByCart(@Param("product")Product product, @Param("member") Member member);
}

