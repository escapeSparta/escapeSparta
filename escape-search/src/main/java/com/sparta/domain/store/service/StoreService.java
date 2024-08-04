package com.sparta.domain.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.KafkaStoreResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.global.exception.customException.KafkaException;
import com.sparta.global.exception.errorCode.KafkaErrorCode;
import com.sparta.global.kafka.KafkaTopic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {

    private final KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;

    /**
     * 방탈출 카페 조회
     * @param pageNum 페이지 번호
     * @param pageSize 페이지에 담는 데이터 수
     * @param isDesc 오름차순, 내림차순 정렬 기준
     * @param keyWord 검색 키워드
     * @param storeRegion 카페 지역
     * @param sort 속성별 정렬 기준
     * @return Store 리스트
     */
    public Page<StoreResponseDto> getStores(int pageNum, int pageSize, boolean isDesc,
                                            String keyWord, StoreRegion storeRegion, String sort) {

        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Page<StoreResponseDto>> future = new CompletableFuture<>();
        responseFutures.put(requestId, future);
        log.error("{}", responseFutures.keySet());
        log.error("StoreService responseFutures hash: {}", System.identityHashCode(responseFutures));
        sendReviewRequest(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }catch (TimeoutException e) {
            throw new KafkaException(KafkaErrorCode.KAFKA_ERROR);
        } finally {
            responseFutures.remove(requestId); // 응답을 받지 못했거나 에러가 발생하면 future를 제거
        }

    }

    private void sendReviewRequest(String requestId, int pageNum, int pageSize, boolean isDesc, String keyWord, StoreRegion storeRegion, String sort) {
        KafkaStoreRequestDto reviewRequest = new KafkaStoreRequestDto(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
        kafkaTemplate.send(KafkaTopic.STORE_REQUEST_TOPIC, reviewRequest);
    }
}
