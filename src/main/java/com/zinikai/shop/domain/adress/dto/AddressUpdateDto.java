package com.zinikai.shop.domain.adress.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressUpdateDto {

    private String zipcode;
    private String state;
    private String city;
    private String street;



    @Builder
    public AddressUpdateDto(String zipcode, String state, String city, String street) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
