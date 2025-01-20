package com.zinikai.shop.domain.member.entity;


import jakarta.persistence.Embeddable;
import lombok.RequiredArgsConstructor;

@Embeddable
public class Address {
    private String street;
    private String city;
    private String zipcode;

    public Address() {
    }

    public Address(String street, String city, String zipcode) {
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
    }

}
