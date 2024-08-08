package com.sparta.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReviewRequestDto{
    private String requestId;
    private Long storeId;
    private Long themeId;
}
