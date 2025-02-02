package com.zinikai.shop.domain.member.dto;

import com.zinikai.shop.domain.member.entity.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private String street;
    private String city;
    private String zipcode;


    public AddressResponseDto(Address address) {
        this.street = address.getStreet();
        this.city = address.getCity();
        this.zipcode = address.getZipcode();
    }

}
