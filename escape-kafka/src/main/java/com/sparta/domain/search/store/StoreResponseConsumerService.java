package com.sparta.domain.search.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.domain.store.dto.KafkaStoreResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreResponseConsumerService {

    private final ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopic.STORE_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleStoreResponse(String response) {
        KafkaStoreResponseDto responseDto = parseMessage(response);
        CompletableFuture<Page<StoreResponseDto>> future = responseFutures.remove(Objects.requireNonNull(responseDto).getRequestId());
        if (future != null) {
            log.error("@@@@");
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
