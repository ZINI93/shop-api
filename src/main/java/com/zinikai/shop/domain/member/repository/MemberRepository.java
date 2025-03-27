package com.zinikai.shop.domain.member.repository;

import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> ,MemberRepositoryCustom{
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberUuid(String uuid);

}