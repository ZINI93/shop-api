package com.zinikai.shop.domain.member.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true,name = "phone_number")
    private String phoneNumber;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "member_uuid",unique = true, nullable = false, updatable = false)
    private String memberUuid;


    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;


    @Builder
    public Member(String email, String password, String name, String phoneNumber, Address address, MemberRole role, String memberUuid) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.memberUuid = UUID.randomUUID().toString();
    }

    public MemberResponseDto toResponseDto() {
        return MemberResponseDto.builder()
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


    public void increaseBalance(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }
}
