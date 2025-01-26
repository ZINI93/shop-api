package com.zinikai.shop.domain.member.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;  // login Id 활용

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder
    public Member(Long id, String email, String password, String name, String phoneNumber, Address address, MemberRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public MemberResponseDto toResponseDto() {
        return MemberResponseDto.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .role(this.role)
                .build();
    }


    //アップデート
    public void updateInfo(String password, String name, String phoneNumber, Address address) {
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
