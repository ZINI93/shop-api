package com.zinikai.shop.domain.member.dto;

import com.zinikai.shop.domain.member.entity.Address;
import com.zinikai.shop.domain.member.entity.MemberRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRequestDto {

    @NotBlank @Email
    private String email;

    @NotBlank
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
//            message = "パスワードは8～20文字で、数字、大文字、小文字、特殊文字を含める必要があります。")
    private String password;

    @NotBlank(message = "お名前を入力してください。")
    private String name;

    @NotBlank(message = "電話番号を入力してください。")
    @Pattern(regexp = "^0[789]0-\\d{4}-\\d{4}$", message = "有効な電話番号を入力してください。")
    private String phoneNumber;

    @NotBlank
    @Valid
    private Address address; // Address 클래스 활용

    @NotBlank
    private MemberRole role; // 기본값 설정이 필요하다면 ENUM 기본값 추가 가능

    @Builder
    public MemberRequestDto(String email, String password, String name, String phoneNumber, Address address, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
}
