package com.sparta.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StoreRegisterRequestDto {
    @NotBlank(message = "방탈출 카페 이름은 필수값입니다.")
    private String name;

    @NotBlank(message = "방탈출 카페 주소는 필수값입니다.")
    private String address;

    @NotBlank(message = "대표 전화번호는 필수값입니다.")
    private String phoneNumber;

    @NotBlank(message = "영업 시간은 필수값입니다.")
    private String workHours;
}