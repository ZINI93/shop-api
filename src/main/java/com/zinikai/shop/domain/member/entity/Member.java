package com.zinikai.shop.domain.member.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "member_uuid",unique = true, nullable = false, updatable = false)
    private String memberUuid;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder
    public Member(String email, String password, String name, String phoneNumber, MemberRole role, String memberUuid) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = MemberRole.USER;
        this.memberUuid = UUID.randomUUID().toString();
    }

    public MemberResponseDto toResponseDto() {
        return MemberResponseDto.builder()
                .email(this.email)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .role(this.role)
                .build();
    }
    
    //アップデート
    public void updateInfo(String password, String name, String phoneNumber) {
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    //販売金　増加
    public void increaseBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be a positive value");
        }
        this.balance = this.balance.add(amount);
    }
}
