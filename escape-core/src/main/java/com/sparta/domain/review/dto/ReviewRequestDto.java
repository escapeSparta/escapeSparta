package com.sparta.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    @NotNull(message = "방탈출 카페  id가 없습니다.")
    private Long storeId;

    @NotNull(message = "방탈출 카페 테마 id가 없습니다.")
    private Long themeId;
}
