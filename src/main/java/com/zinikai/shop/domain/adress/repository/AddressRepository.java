package com.zinikai.shop.domain.adress.repository;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByMemberMemberUuidAndAddressUuid(String ownerUuid, String addressUuid);
    Optional<Address> findByMemberMemberUuid(String ownerUuid);
    int countByMember(Member member);

}
