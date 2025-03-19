package com.zinikai.shop.domain.adress.service;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    @Override @Transactional
    public AddressResponseDto createAddress(Long memberId, AddressRequestDto requestDto) {

        log.info("Creating address for member Id:{}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Not found Member ID"));

        if (!Objects.equals(member.getId(), memberId)) {
            throw new IllegalArgumentException("MemberShip IDs do not match");
        }

        Address address = Address.builder()
                .member(member)
                .zipcode(requestDto.getZipcode())
                .state(requestDto.getState())
                .city(requestDto.getCity())
                .street(requestDto.getStreet())
                .build();

        log.info("Created address: {}", address);

        Address savedAddress = addressRepository.save(address);

        return savedAddress.toResponseDto();

    }

    @Override
    public AddressResponseDto getAddress(String memberUuid, String addressUuid) {

        Address address = addressRepository.findByMemberMemberUuidAndAddressUuid(memberUuid, addressUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or address UUID"));

        matchOwnerUUidAndAddressUuid(memberUuid,addressUuid,address);

        return address.toResponseDto();
    }

    @Override @Transactional
    public AddressResponseDto updateAddress(String memberUuid, String addressUuid, AddressUpdateDto updateDto) {

        log.info("Updating address for member UUID :{}, address UUID :{}", memberUuid, addressUuid);

        Address address = addressRepository.findByMemberMemberUuidAndAddressUuid(memberUuid, addressUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or address UUID"));

        matchOwnerUUidAndAddressUuid(memberUuid, addressUuid, address);

        address.updateInfo(
                updateDto.getZipcode(),
                updateDto.getState(),
                updateDto.getCity(),
                updateDto.getStreet());

        log.info("Updated address :{}", address);

        return address.toResponseDto();
    }

    @Override @Transactional
    public void deleteAddress(String memberUuid, String addressUuid) {

        log.info("Deleting address for member UUID :{}, address UUID :{}", memberUuid, addressUuid);

        Address address = addressRepository.findByMemberMemberUuidAndAddressUuid(memberUuid, addressUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or address UUID"));

        matchOwnerUUidAndAddressUuid(memberUuid, addressUuid, address);

        addressRepository.delete(address);

    }

    private static void matchOwnerUUidAndAddressUuid(String memberUuid, String addressUuid, Address address) {
        if (!Objects.equals(address.getMember().getMemberUuid(), memberUuid)) {
            throw new IllegalArgumentException("Owner UUID dose not match the address owner");
        }
        if (!Objects.equals(address.getAddressUuid(), addressUuid))
            throw new IllegalArgumentException("Address UUID dose not match");
    }
}
