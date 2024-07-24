package com.sparta.dto.request;

import com.sparta.domain.theme.entity.ThemeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ThemeModifyRequestDto {

    @NotBlank(message = "방탈출 테마 제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "방탈출 테마 설명은 필수값입니다.")
    private String contents;

    @NotNull(message = "방탈출 테마 난이도는 필수값입니다.")
    @Min(value = 1)
    @Max(value = 5)
    private Long level;

    @NotBlank(message = "방탈출 테마 플레이 시간은 필수값입니다.")
    private String duration;

    @NotNull(message = "플레이 최소 인원은 필수값입니다.")
    @Min(value = 1)
    private Integer minPlayer;

    @NotNull(message = "플레이 최대 인원은 필수값입니다.")
    @Min(value = 1)
    private Integer maxPlayer;

    @NotNull(message = "방탈출 테마의 장르는 필수값입니다.")
    private ThemeType themeType;

    @NotNull(message = "가격은 필수값입니다.")
    @Min(value = 1)
    private Long price;
}
