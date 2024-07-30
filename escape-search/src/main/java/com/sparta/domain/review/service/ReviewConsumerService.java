package com.sparta.domain.review.service;

import com.sparta.domain.review.dto.KafkaReviewRequestDto;
import com.sparta.domain.review.dto.KafkaReviewResponseDto;
import com.sparta.domain.review.dto.ReviewResponseDto;
import com.sparta.domain.review.entity.Review;
import com.sparta.domain.review.repository.ReviewRepository;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.repository.ThemeRepository;
import com.sparta.global.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumerService {
    private final ReviewRepository reviewRepository;
    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;
    private final KafkaTemplate<String, KafkaReviewResponseDto> kafkaTemplate;

    @KafkaListener(topics = KafkaTopic.REVIEW_REQUEST_TOPIC, groupId = "review-consumer-group")
    public void handleReviewRequest(KafkaReviewRequestDto reviewRequest) {
        storeRepository.findByActiveStore(reviewRequest.getStoreId());
        Theme theme = themeRepository.findByActiveTheme(reviewRequest.getThemeId());
        List<Review> reviewList = reviewRepository.findByThemeReview(theme);

        List<ReviewResponseDto> responseDtoList = reviewList.stream().map(ReviewResponseDto::new).toList();

        KafkaReviewResponseDto reviewResponse = new KafkaReviewResponseDto(reviewRequest.getRequestId(), responseDtoList);
        kafkaTemplate.send(KafkaTopic.REVIEW_RESPONSE_TOPIC, reviewResponse);
    }
}
