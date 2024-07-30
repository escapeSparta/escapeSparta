package com.sparta.domain.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.KafkaStoreResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {

    private final KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

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
        sendReviewRequest(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);

        try {
            return future.get(); // 응답을 기다림
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("방탈출 카페 response 실패", e);
        }

    }

    private void sendReviewRequest(String requestId, int pageNum, int pageSize, boolean isDesc, String keyWord, StoreRegion storeRegion, String sort) {
        KafkaStoreRequestDto reviewRequest = new KafkaStoreRequestDto(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
        kafkaTemplate.send(KafkaTopic.STORE_REQUEST_TOPIC, reviewRequest);
    }

    @KafkaListener(topics = KafkaTopic.STORE_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleStoreResponse(String response) {
        KafkaStoreResponseDto responseDto = parseMessage(response);
        CompletableFuture<Page<StoreResponseDto>> future = responseFutures.remove(Objects.requireNonNull(responseDto).getRequestId());
        if (future != null) {
            future.complete(responseDto.getResponseDtos());
        }
    }

    private KafkaStoreResponseDto parseMessage(String message) {
        try {
            return objectMapper.readValue(message, KafkaStoreResponseDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
