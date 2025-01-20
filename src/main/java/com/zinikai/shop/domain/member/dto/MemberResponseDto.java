package com.zinikai.shop.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.zinikai.shop.domain.member.entity.Address;
import com.zinikai.shop.domain.member.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.web.ProjectedPayload;

@Data
@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private Address address;
    private MemberRole role;

    @Builder
    public MemberResponseDto(Long id, String email, String name, String phoneNumber, Address address, MemberRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }


    @QueryProjection
    public MemberResponseDto(Long id, String email, String name, String phoneNumber,Address address) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
