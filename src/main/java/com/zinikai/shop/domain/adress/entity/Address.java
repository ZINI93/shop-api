package com.zinikai.shop.domain.adress.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(name = "address_uuid", nullable = false, updatable = false, unique = false)
    private String addressUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;


    @Builder
    public Address(String zipcode, String state, String city, String street, Member member, String addressUuid) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.member = member;
        this.addressUuid = UUID.randomUUID().toString();
    }

    public AddressResponseDto toResponseDto(){
        return AddressResponseDto.builder()
                .zipcode(this.zipcode)
                .state(this.state)
                .city(this.city)
                .street(this.street)
                .AddressUuid(this.getAddressUuid())
                .build();
    }

    public void updateInfo(String zipcode, String state, String city, String street) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }

}
