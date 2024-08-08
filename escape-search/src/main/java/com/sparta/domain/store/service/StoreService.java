package com.sparta.domain.store.service;

import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.global.exception.customException.KafkaException;
import com.sparta.global.exception.errorCode.KafkaErrorCode;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
        sendStoreRequest(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);

        try {
            return future.get(3, TimeUnit.SECONDS); // 응답을 기다림
        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaException(KafkaErrorCode.KAFKA_SERVER_ERROR);
        }catch (TimeoutException e){
            throw new KafkaException(KafkaErrorCode.KAFKA_RESPONSE_ERROR);
        }
    }

    private void sendStoreRequest(String requestId, int pageNum, int pageSize, boolean isDesc, String keyWord, StoreRegion storeRegion, String sort) {
        KafkaStoreRequestDto storeRequest = new KafkaStoreRequestDto(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
        kafkaTemplate.send(KafkaTopic.STORE_REQUEST_TOPIC, storeRequest);
    }
}
