package com.sparta.domain.theme.dto;

import com.sparta.domain.store.dto.StoreResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaThemeResponseDto {
    private String requestId;
    private Page<ThemeResponseDto> responseDtos;
}
