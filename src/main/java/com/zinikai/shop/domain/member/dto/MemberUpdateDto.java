package com.zinikai.shop.domain.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Data
@Builder
public class MemberUpdateDto {

    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "パスワードは8～20文字で、数字、大文字、小文字、特殊文字を含める必要があります。")
    private String password;

    @NotBlank(message = "お名前を入力してください。")
    private String name;

    @NotBlank(message = "電話番号を入力してください。")
    @Pattern(regexp = "^0[789]0-\\d{4}-\\d{4}$", message = "有効な電話番号を入力してください。")
    private String phoneNumber;


    public MemberUpdateDto(String password, String name, String phoneNumber) {
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
