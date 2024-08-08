package com.sparta.domain.store.service;

import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.KafkaStoreResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.global.exception.customException.GlobalCustomException;
import com.sparta.global.kafka.KafkaTopic;
import com.sparta.global.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreConsumerService {
    private final StoreRepository storeRepository;
    private final ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;

    @KafkaListener(topics = KafkaTopic.STORE_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleStoreRequest(KafkaStoreRequestDto request) {
        try {
            Pageable pageable = PageUtil.createPageable(request.getPageNum(), request.getPageSize(), request.isDesc(), request.getSort());
            Page<Store> stores = storeRepository.findByName(request.getKeyWord(), request.getStoreRegion(), pageable);
            Page<StoreResponseDto> storeResponseDtoPage = stores.map(StoreResponseDto::new);
            KafkaStoreResponseDto response = new KafkaStoreResponseDto(request.getRequestId(), storeResponseDtoPage);

            handleStoreResponse(response);
        }catch (GlobalCustomException e){
            log.error(e.getMessage());
        }
    }

    private void handleStoreResponse(KafkaStoreResponseDto response) {
        CompletableFuture<Page<StoreResponseDto>> future = responseFutures.remove(response.getRequestId());
        if (future != null) {
            future.complete(response.getResponseDtos());
        }
    }
}
