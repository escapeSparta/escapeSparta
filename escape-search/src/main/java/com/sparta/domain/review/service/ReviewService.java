package com.sparta.domain.review.service;

import com.sparta.domain.review.dto.KafkaReviewRequestDto;
import com.sparta.domain.review.dto.ReviewResponseDto;
import com.sparta.global.exception.customException.KafkaException;
import com.sparta.global.exception.errorCode.KafkaErrorCode;
import com.sparta.global.kafka.KafkaTopic;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

  private final KafkaTemplate<String, KafkaReviewRequestDto> kafkaTemplate;
  private final ConcurrentHashMap<String, CompletableFuture<List<ReviewResponseDto>>> responseFutures;

  /**
   * 방탈출 카페 테마 리뷰 조회
   *
   * @param storeId 방탈출 카페 id
   * @param themeId 테마 id가 들어있는 dto
   * @return 리뷰 반환
   */
  public List<ReviewResponseDto> getReview(Long storeId, Long themeId) {
    String requestId = UUID.randomUUID().toString();

    CompletableFuture<List<ReviewResponseDto>> future = new CompletableFuture<>();
    responseFutures.put(requestId, future);

    sendReviewRequest(requestId, storeId, themeId);

    // Kafka로 요청을 전송하고 응답을 비동기적으로 기다림
    try {
      return future.get(3, TimeUnit.SECONDS); // 응답을 기다림
    } catch (InterruptedException | ExecutionException e) {
      throw new KafkaException(KafkaErrorCode.KAFKA_SERVER_ERROR);
    } catch (TimeoutException e) {
      throw new KafkaException(KafkaErrorCode.KAFKA_RESPONSE_ERROR);
    }
  }

  private void sendReviewRequest(String requestId, Long storeId, Long themeId) {
    KafkaReviewRequestDto reviewRequest = new KafkaReviewRequestDto(requestId, storeId, themeId);
    kafkaTemplate.send(KafkaTopic.REVIEW_REQUEST_TOPIC, reviewRequest);
  }
}

