package com.zinikai.shop.domain.member.dto;

import com.zinikai.shop.domain.member.entity.Address;
import com.zinikai.shop.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Data;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Data
@Builder
public class MemberUpdateDto {
    private String password;
    private String name;
    private String phoneNumber;
    private Address address; // Address 클래스 활용


    public MemberUpdateDto(String password, String name, String phoneNumber, Address address) {
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
