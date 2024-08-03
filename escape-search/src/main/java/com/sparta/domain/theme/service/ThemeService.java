package com.sparta.domain.theme.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.domain.theme.dto.*;
import com.sparta.global.exception.customException.KafkaException;
import com.sparta.global.exception.errorCode.KafkaErrorCode;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeService {

    private final KafkaTemplate<String, KafkaThemeRequestDto> kafkaThemeTemplate;
    private final KafkaTemplate<String, KafkaThemeInfoRequestDto> kafkaThemeInfoTemplate;
    private final KafkaTemplate<String, KafkaThemeTimeRequestDto> kafkaThemeTimeTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Page<ThemeResponseDto>>> responseThemeFutures;
    private final ConcurrentHashMap<String, CompletableFuture<ThemeInfoResponseDto>> responseThemeInfoFutures;
    private final ConcurrentHashMap<String, CompletableFuture<List<ThemeTimeResponseDto>>> responseThemeTimeFutures;

    /**
     * 방탈출 카페 테마 전체 조회
     * @param storeId 방탈출 카페 id
     * @param pageNum 페이지 번호
     * @param pageSize 페이지에 담는 데이터 수
     * @param isDesc 오름차순, 내림차순 정렬 기준
     * @param sort 속성별 정렬 기준
     * @return EscapeRoom 리스트
     */
    public Page<ThemeResponseDto> getTheme(Long storeId, int pageNum, int pageSize, boolean isDesc, String sort) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Page<ThemeResponseDto>> future = new CompletableFuture<>();
        responseThemeFutures.put(requestId, future);
        sendReviewRequest(requestId, storeId, pageNum, pageSize, isDesc, sort);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }catch (TimeoutException e) {
            throw new KafkaException(KafkaErrorCode.KAFKA_ERROR);
        } finally {
            responseThemeFutures.remove(requestId); // 응답을 받지 못했거나 에러가 발생하면 future를 제거
        }
    }

    private void sendReviewRequest(String requestId, Long storeId, int pageNum, int pageSize, boolean isDesc, String sort) {
        KafkaThemeRequestDto reviewRequest = new KafkaThemeRequestDto(requestId, storeId, pageNum, pageSize, isDesc, sort);
        kafkaThemeTemplate.send(KafkaTopic.THEME_REQUEST_TOPIC, reviewRequest);
    }

    /**
     * 방탈출 카페 테마 상세 조회
     * @param storeId 해당 카페 id
     * @param themeId 해당 카페의 테마 id
     * @return theme 정보 반환
     */
    public ThemeInfoResponseDto getThemeInfo(Long storeId, Long themeId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ThemeInfoResponseDto> future = new CompletableFuture<>();
        responseThemeInfoFutures.put(requestId, future);
        sendThemeInfoRequest(requestId, storeId, themeId);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }catch (TimeoutException e) {
            throw new KafkaException(KafkaErrorCode.KAFKA_ERROR);
        } finally {
            responseThemeInfoFutures.remove(requestId); // 응답을 받지 못했거나 에러가 발생하면 future를 제거
        }
    }

    private void sendThemeInfoRequest(String requestId, Long storeId, Long themeId) {
        KafkaThemeInfoRequestDto reviewRequest = new KafkaThemeInfoRequestDto(requestId, storeId, themeId);
        kafkaThemeInfoTemplate.send(KafkaTopic.THEME_INFO_REQUEST_TOPIC, reviewRequest);
    }

    /**
     * 방탈출 카페 테마 시간 조회
     * @param storeId 검색할 테마의 스토어 id
     * @param themeId 해당 카페의 테마 id
     * @return theme 시간 반환
     */
    public List<ThemeTimeResponseDto> getThemeTime(Long storeId, Long themeId, String day) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<List<ThemeTimeResponseDto>> future = new CompletableFuture<>();
        responseThemeTimeFutures.put(requestId, future);
        sendThemeTimeRequest(requestId, storeId, themeId, day);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }catch (TimeoutException e) {
            throw new KafkaException(KafkaErrorCode.KAFKA_ERROR);
        } finally {
            responseThemeTimeFutures.remove(requestId); // 응답을 받지 못했거나 에러가 발생하면 future를 제거
        }
    }

    private void sendThemeTimeRequest(String requestId, Long storeId, Long themeId, String day) {
        KafkaThemeTimeRequestDto Request = new KafkaThemeTimeRequestDto(requestId, storeId, themeId, day);
        kafkaThemeTimeTemplate.send(KafkaTopic.THEME_TIME_REQUEST_TOPIC, Request);
    }
}
