package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.adress.service.AddressService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequestMapping("/api/address")
@RequiredArgsConstructor
@RestController
public class AddressApiController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto requestDto,
                                                            Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        AddressResponseDto address = addressService.createAddress(memberUuid, requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{addressUuid}")
                .buildAndExpand(address.getAddressUuid())
                .toUri();
        return ResponseEntity.created(location).body(address);
    }

    @GetMapping("{addressUuid}")
    public ResponseEntity<AddressResponseDto> getAddress(Authentication authentication,
                                                         @PathVariable String addressUuid) {

        String memberUuid = getMemberUuid(authentication);

        AddressResponseDto address = addressService.getAddress(memberUuid, addressUuid);

        return ResponseEntity.ok(address);

    }

    @PutMapping("{addressUuid}")
    public ResponseEntity<AddressResponseDto> updateAddress(@RequestBody AddressUpdateDto UpdateDto,
                                                            @PathVariable String addressUuid,
                                                            Authentication authentication) {
        String memberUuid = getMemberUuid(authentication);

        AddressResponseDto updateAddress = addressService.updateAddress(memberUuid, addressUuid, UpdateDto);

        return ResponseEntity.ok(updateAddress);
    }

    @DeleteMapping("{addressUuid}")
    public ResponseEntity<AddressResponseDto> deleteAddress(@PathVariable String addressUuid,
                                                            Authentication authentication) {

        String memberUuid = getMemberUuid(authentication);

        addressService.deleteAddress(memberUuid, addressUuid);

        return ResponseEntity.noContent().build();
    }

    private String getMemberUuid(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getMemberUuid();
    }
}
