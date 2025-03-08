package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 여기는 실제 DB에서 조회하도록 구현 (JPA 등)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("로그인 시도: " + email);
        return memberRepository.findByEmail(email)
                .map(member -> {
                    System.out.println("DB에서 찾은 사용자: " + member.getEmail());
                    return new CustomUserDetails(member);
                })
                .orElseThrow(() -> {
                    System.out.println("사용자를 찾을 수 없음: " + email);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
