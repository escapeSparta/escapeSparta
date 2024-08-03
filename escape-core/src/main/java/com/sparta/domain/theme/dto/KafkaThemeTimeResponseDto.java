package com.sparta.domain.theme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaThemeTimeResponseDto {
    private String requestId;
    private List<ThemeTimeResponseDto> responseDtoList;
}
