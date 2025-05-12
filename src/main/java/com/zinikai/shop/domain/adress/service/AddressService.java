package com.zinikai.shop.domain.adress.service;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.member.entity.Member;

public interface AddressService {

    Address createAddress(Member member, AddressRequestDto requestDto);

    AddressResponseDto createAddressWithValidation(String memberUuid, AddressRequestDto requestDto);

    AddressResponseDto getAddress(String memberUuid, String addressUuid);

    AddressResponseDto updateAddress(String memberUuid, String addressUuid, AddressUpdateDto updateDto);

    void deleteAddress(String memberUuid, String addressUuid);


}
