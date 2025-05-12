package com.zinikai.shop.domain.adress.repository;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("select a from Address a join fetch a.member m where m.memberUuid = :memberUuid and a.addressUuid = :addressUuid")
    Optional<Address> findByMemberMemberUuidAndAddressUuid(@Param("memberUuid") String memberUuid, @Param("addressUuid") String addressUuid);

    @Query("select a from Address a join fetch a.member m where m.memberUuid = :memberUuid")
    Optional<Address> findByMemberMemberUuid(@Param("memberUuid") String ownerUuid);

    int countByMember(Member member);

}
