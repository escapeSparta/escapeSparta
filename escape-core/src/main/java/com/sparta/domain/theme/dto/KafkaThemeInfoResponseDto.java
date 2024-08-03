package com.sparta.domain.theme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaThemeInfoResponseDto {
    private String requestId;
    private ThemeInfoResponseDto responseDto;
}
