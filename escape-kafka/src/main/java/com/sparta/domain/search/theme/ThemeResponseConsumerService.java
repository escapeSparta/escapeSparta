package com.sparta.domain.search.theme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.domain.theme.dto.*;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeResponseConsumerService {

    private final ConcurrentHashMap<String, CompletableFuture<Page<ThemeResponseDto>>> responseThemeFutures;
    private final ConcurrentHashMap<String, CompletableFuture<ThemeInfoResponseDto>> responseThemeInfoFutures;
    private final ConcurrentHashMap<String, CompletableFuture<List<ThemeTimeResponseDto>>> responseThemeTimeFutures;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopic.THEME_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeResponse(String response) {
        KafkaThemeResponseDto responseDto = parseThemeMessage(response);
        CompletableFuture<Page<ThemeResponseDto>> future = responseThemeFutures.remove(Objects.requireNonNull(responseDto).getRequestId());
        if (future != null) {
            log.error("####");
            future.complete(responseDto.getResponseDtos());
        }
    }

    private KafkaThemeResponseDto parseThemeMessage(String message) {
        try {
            return objectMapper.readValue(message, KafkaThemeResponseDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_INFO_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeInfoResponse(KafkaThemeInfoResponseDto response) {
        CompletableFuture<ThemeInfoResponseDto> future = responseThemeInfoFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            log.error("$$$$");
            future.complete(response.getResponseDto());
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_TIME_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeTimeResponse(KafkaThemeTimeResponseDto response) {
        CompletableFuture<List<ThemeTimeResponseDto>> future = responseThemeTimeFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            log.error("%%%%");
            future.complete(response.getResponseDtoList());
        }
    }
}
