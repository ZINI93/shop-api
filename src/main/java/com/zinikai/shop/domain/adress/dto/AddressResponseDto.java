package com.zinikai.shop.domain.adress.dto;
import lombok.*;

@Data
@Builder
public class AddressResponseDto {

    private Long Id;
    private Long memberId;
    private String zipcode;
    private String state;
    private String city;
    private String street;

    @Builder
    public AddressResponseDto(Long id, Long memberId, String zipcode, String state, String city, String street) {
        this.Id = id;
        this.memberId = memberId;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
