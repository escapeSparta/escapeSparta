package com.sparta.domain.theme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaThemeTimeRequestDto {
    private String requestId;
    private Long storeId;
    private Long themeId;
    private String day;
}
