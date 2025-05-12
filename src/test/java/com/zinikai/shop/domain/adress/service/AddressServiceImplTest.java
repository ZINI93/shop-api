package com.zinikai.shop.domain.adress.service;

import com.zinikai.shop.domain.adress.dto.AddressRequestDto;
import com.zinikai.shop.domain.adress.dto.AddressResponseDto;
import com.zinikai.shop.domain.adress.dto.AddressUpdateDto;
import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {


    @Mock AddressRepository addressRepository;
    @Mock MemberRepository memberRepository;
    @InjectMocks AddressServiceImpl addressService;

    Member member;
    Address address;

    AddressRequestDto requestDto;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        setMemberId(member, 1L);

        address = new Address(
                "123-123",
                "kanagawaken",
                "kawasakisi miyamaeku",
                "higasiarima 1-1-1",
                member,
                UUID.randomUUID().toString()
        );

        requestDto = new AddressRequestDto(
                address.getZipcode(),
                address.getState(),
                address.getCity(),
                address.getStreet()
        );
    }

    @DisplayName("ご住所を追加")
    @Test
    void testCreateAddress() {
        //given
        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        //when
        AddressResponseDto result = addressService.createAddressWithValidation(member.getMemberUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(requestDto.getZipcode(), result.getZipcode());

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @DisplayName("ご住所を変更")
    @Test
    void updateAddress() {

        //given
        when(addressRepository.findByMemberMemberUuidAndAddressUuid(address.getMember().getMemberUuid(), address.getAddressUuid())).thenReturn(Optional.ofNullable(address));

        //when

        AddressUpdateDto updateDto = new AddressUpdateDto("222-222", "saitama", "mora", "1-1-1");
        AddressResponseDto result = addressService.updateAddress(address.getMember().getMemberUuid(), address.getAddressUuid(), updateDto);

        //then

        assertNotNull(result);
        assertEquals(updateDto.getCity(), result.getCity());
        assertEquals(updateDto.getStreet(), result.getStreet());

        verify(addressRepository, times(1)).findByMemberMemberUuidAndAddressUuid(address.getMember().getMemberUuid(), address.getAddressUuid());
    }

    @DisplayName("住所を削除")
    @Test
    void deleteAddress() {

        //given
        when(addressRepository.findByMemberMemberUuidAndAddressUuid(address.getMember().getMemberUuid(), address.getAddressUuid())).thenReturn(Optional.ofNullable(address));

        //when
        addressService.deleteAddress(address.getMember().getMemberUuid(),address.getAddressUuid());

        //then
        verify(addressRepository, times(1)).findByMemberMemberUuidAndAddressUuid(address.getMember().getMemberUuid(), address.getAddressUuid());

    }
}
