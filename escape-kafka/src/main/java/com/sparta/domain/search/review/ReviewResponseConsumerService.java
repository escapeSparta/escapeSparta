package com.sparta.domain.search.review;

import com.sparta.domain.review.dto.KafkaReviewResponseDto;
import com.sparta.domain.review.dto.ReviewResponseDto;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewResponseConsumerService {

    private final ConcurrentHashMap<String, CompletableFuture<List<ReviewResponseDto>>> responseFutures;

    @KafkaListener(topics = KafkaTopic.REVIEW_RESPONSE_TOPIC, groupId = "${GROUP_SEARCH_ID}")
    public void handleReviewResponse(KafkaReviewResponseDto reviewResponse) {
        CompletableFuture<List<ReviewResponseDto>> future = responseFutures.remove(reviewResponse.getRequestId());
        if (future != null) {
            log.error("!!!!");
            future.complete(reviewResponse.getReviewResponses());
        }
    }
}
