package com.sparta.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReviewResponseDto {
    private String requestId;
    private List<ReviewResponseDto> reviewResponses;
}
