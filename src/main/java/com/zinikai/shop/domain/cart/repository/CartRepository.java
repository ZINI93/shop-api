package com.zinikai.shop.domain.cart.repository;

import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Page<CartResponseDto> findAllByMemberMemberUuid(String memberUuid, Pageable pageable);
    Optional<Cart> findByMemberMemberUuidAndCartUuid(String ownerUuid, String cartUuid);
    Optional<Cart> findByMemberMemberUuidAndProductId(String memberUuid, Long productId);
}

