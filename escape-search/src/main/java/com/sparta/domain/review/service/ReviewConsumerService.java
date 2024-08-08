package com.sparta.domain.review.service;

import com.sparta.domain.review.dto.KafkaReviewRequestDto;
import com.sparta.domain.review.dto.KafkaReviewResponseDto;
import com.sparta.domain.review.dto.ReviewResponseDto;
import com.sparta.domain.review.entity.Review;
import com.sparta.domain.review.repository.ReviewRepository;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.repository.ThemeRepository;
import com.sparta.global.exception.customException.GlobalCustomException;
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
public class ReviewConsumerService {
    private final ReviewRepository reviewRepository;
    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;
    private final ConcurrentHashMap<String, CompletableFuture<List<ReviewResponseDto>>> responseFutures;

    @KafkaListener(topics = KafkaTopic.REVIEW_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleReviewRequest(KafkaReviewRequestDto reviewRequest) {
        try {
            storeRepository.findByActiveStore(reviewRequest.getStoreId());
            Theme theme = themeRepository.findByActiveTheme(reviewRequest.getThemeId());
            List<Review> reviewList = reviewRepository.findByThemeReview(theme);

            List<ReviewResponseDto> responseDtoList = reviewList.stream().map(ReviewResponseDto::new).toList();

            KafkaReviewResponseDto reviewResponse = new KafkaReviewResponseDto(reviewRequest.getRequestId(), responseDtoList);
            handleReviewResponse(reviewResponse);
        }catch (GlobalCustomException e){
            log.error(e.getMessage());
        }
    }

    private void handleReviewResponse(KafkaReviewResponseDto reviewResponse) {
        CompletableFuture<List<ReviewResponseDto>> future = responseFutures.remove(Objects.requireNonNull(reviewResponse).getRequestId());
        if (future != null) {
            future.complete(reviewResponse.getReviewResponses());
        }
    }
}
