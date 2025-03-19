package com.zinikai.shop.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private MemberRole role;

    @Builder
    public MemberResponseDto(Long id, String email, String name, String phoneNumber, MemberRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }


    @QueryProjection
    public MemberResponseDto(Long id, String email, String name, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
