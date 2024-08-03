package com.sparta.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaStoreResponseDto {
    private String requestId;
    private Page<StoreResponseDto> responseDtos;
}
