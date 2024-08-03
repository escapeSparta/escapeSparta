package com.sparta.domain.theme.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaThemeRequestDto {
    private String requestId;
    private Long storeId;
    private int pageNum;
    private int pageSize;
    private boolean isDesc;
    private String sort;
}
