package com.zinikai.shop.domain.adress.dto;
import lombok.*;

@Data
@Builder
public class AddressResponseDto {

    private String zipcode;
    private String state;
    private String city;
    private String street;
    private String AddressUuid;

    @Builder
    public AddressResponseDto(String zipcode, String state, String city, String street, String addressUuid) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        AddressUuid = addressUuid;
    }
}
