package com.zinikai.shop.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseDto {
    private String email;
    private String name;
    private String phoneNumber;
    private String memberUuid;


    @QueryProjection
    public MemberResponseDto(String email, String name, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }


    @Builder
    public MemberResponseDto(String email, String name, String phoneNumber, String memberUuid) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memberUuid = memberUuid;
    }
}
