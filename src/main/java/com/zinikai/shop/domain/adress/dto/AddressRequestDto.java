package com.zinikai.shop.domain.adress.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Primary;

@Data
@Builder
public class AddressRequestDto {

    private String zipcode;
    private String state;
    private String city;
    private String street;

    @Builder
    public AddressRequestDto(String zipcode, String state, String city, String street) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
