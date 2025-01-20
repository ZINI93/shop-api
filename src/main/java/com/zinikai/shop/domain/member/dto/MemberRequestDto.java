package com.zinikai.shop.domain.member.dto;

import com.zinikai.shop.domain.member.entity.Address;
import com.zinikai.shop.domain.member.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class MemberRequestDto {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Address address; // Address 클래스 활용
    private MemberRole role; // 기본값 설정이 필요하다면 ENUM 기본값 추가 가능

    @Builder
    public MemberRequestDto(String email, String password, String name, String phoneNumber, Address address, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
}
