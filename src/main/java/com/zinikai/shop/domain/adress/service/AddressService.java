package com.zinikai.shop.domain.adress.service;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.cart.dto.CartRequestDto;

public interface AddressService {

    AddressResponseDto createAddress(Long memberId, AddressRequestDto requestDto);

    AddressResponseDto getAddress(String memberUuid, String addressUuid);

    AddressResponseDto updateAddress(String memberUuid, String addressUuid, AddressUpdateDto updateDto);

    void deleteAddress(String memberUuid, String addressUuid);


}
