package com.zinikai.shop.domain.adress.service;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.exception.AddressNotFoundException;
import com.zinikai.shop.domain.adress.exception.ValidateAddressException;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
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

    @Override
    @Transactional
    public Address createAddress(Member member, AddressRequestDto requestDto) {

        return Address.builder()
                .member(member)
                .zipcode(requestDto.getZipcode())
                .state(requestDto.getState())
                .city(requestDto.getCity())
                .street(requestDto.getStreet())
                .build();
    }

    @Override
    @Transactional
    public AddressResponseDto createAddressWithValidation(String memberUuid, AddressRequestDto requestDto) {

        log.info("Creating address for member Id:{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);
        validateMaxOneAddressPerMember(member);

        Address address = createAddress(member, requestDto);
        Address savedAddress = addressRepository.save(address);

        log.info("Created address UUID: {}", savedAddress.getAddressUuid());

        return savedAddress.toResponseDto();

    }

    @Override
    public AddressResponseDto getAddress(String memberUuid, String addressUuid) {

        Address address = findMemberByMemberUuidAndAddressUuid(memberUuid, addressUuid);

        validateAddressOwner(memberUuid, address);

        return address.toResponseDto();
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(String memberUuid, String addressUuid, AddressUpdateDto updateDto) {

        log.info("Updating address for member UUID :{}, address UUID :{}", memberUuid, addressUuid);

        Address address = findMemberByMemberUuidAndAddressUuid(memberUuid, addressUuid);
        validateAddressOwner(memberUuid, address);

        address.updateInfo(
                updateDto.getZipcode(),
                updateDto.getState(),
                updateDto.getCity(),
                updateDto.getStreet());

        log.info("Updated address UUID:{}", address.getAddressUuid());

        return address.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteAddress(String memberUuid, String addressUuid) {

        log.info("Deleting address for member UUID :{}, address UUID :{}", memberUuid, addressUuid);

        Address address = findMemberByMemberUuidAndAddressUuid(memberUuid, addressUuid);

        validateAddressOwner(memberUuid, address);

        addressRepository.delete(address);

    }

    private void validateAddressOwner(String memberUuid, Address address) {
        if (!Objects.equals(address.getMember().getMemberUuid(), memberUuid)) {
            throw new ValidateAddressException("Owner UUID does not match the address owner");
        }
    }

    private static final int MAX_ADDRESS_COUNT = 1;

    private void validateMaxOneAddressPerMember(Member member) {
        int currentAddressCount = addressRepository.countByMember(member);

        if (currentAddressCount >= MAX_ADDRESS_COUNT) {
            throw new ValidateAddressException("Member can have only one address. Member Uuid:" + member.getMemberUuid());
        }
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Not found Member ID"));
    }

    private Address findMemberByMemberUuidAndAddressUuid(String memberUuid, String addressUuid) {
        return addressRepository.findByMemberMemberUuidAndAddressUuid(memberUuid, addressUuid)
                .orElseThrow(() -> new AddressNotFoundException("Not found owner UUID or address UUID"));
    }
}
