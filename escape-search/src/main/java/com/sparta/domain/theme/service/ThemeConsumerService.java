package com.sparta.domain.theme.service;

import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.theme.dto.*;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.repository.ThemeRepository;
import com.sparta.domain.theme.repository.ThemeTimeRepository;
import com.sparta.global.exception.customException.GlobalCustomException;
import com.sparta.global.kafka.KafkaTopic;
import com.sparta.global.util.LocalDateTimeUtil;
import com.sparta.global.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeConsumerService {

    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;
    private final ThemeTimeRepository themeTimeRepository;
    private final ConcurrentHashMap<String, CompletableFuture<Page<ThemeResponseDto>>> responseThemeFutures;
    private final ConcurrentHashMap<String, CompletableFuture<ThemeInfoResponseDto>> responseThemeInfoFutures;
    private final ConcurrentHashMap<String, CompletableFuture<List<ThemeTimeResponseDto>>> responseThemeTimeFutures;

    @KafkaListener(topics = KafkaTopic.THEME_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleThemeRequest(KafkaThemeRequestDto request) {
        try {
            Store store = storeRepository.findByIdOrElseThrow(request.getStoreId());

            Pageable pageable = PageUtil.createPageable(request.getPageNum(), request.getPageSize(), request.isDesc(), request.getSort());
            Page<Theme> themes = themeRepository.findByStore(store, pageable);
            Page<ThemeResponseDto> themeResponseDtoPage = themes.map(ThemeResponseDto::new);

            KafkaThemeResponseDto responseDto = new KafkaThemeResponseDto(request.getRequestId(), themeResponseDtoPage);
            handleThemeResponse(responseDto);
        }catch (GlobalCustomException e){
            log.error(e.getMessage());
        }
    }

    private void handleThemeResponse(KafkaThemeResponseDto response) {
        CompletableFuture<Page<ThemeResponseDto>> future = responseThemeFutures.remove(response.getRequestId());
        if (future != null) {
            future.complete(response.getResponseDtos());
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_INFO_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleThemeInfoRequest(KafkaThemeInfoRequestDto request) {
        try {
            storeRepository.findByActiveStore(request.getStoreId());
            Theme theme = themeRepository.findByActiveTheme(request.getThemeId());
            ThemeInfoResponseDto themeInfoResponseDto = new ThemeInfoResponseDto(theme);
            KafkaThemeInfoResponseDto responseDto = new KafkaThemeInfoResponseDto(request.getRequestId(), themeInfoResponseDto);
            handleThemeInfoResponse(responseDto);
        }catch (GlobalCustomException e){
            log.error(e.getMessage());
        }
    }

    private void handleThemeInfoResponse(KafkaThemeInfoResponseDto response) {
        CompletableFuture<ThemeInfoResponseDto> future = responseThemeInfoFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            future.complete(response.getResponseDto());
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_TIME_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleThemeTimeRequest(KafkaThemeTimeRequestDto request) {
        try {
            LocalDate day = LocalDateTimeUtil.parseDateStringToLocalDate(request.getDay());
            storeRepository.findByActiveStore(request.getStoreId());
            List<ThemeTime> themeTimeList = themeTimeRepository.findThemeTimesByDate(request.getThemeId(), day);
            List<ThemeTimeResponseDto> themeTimeResponseDtoList = themeTimeList.stream().map(ThemeTimeResponseDto::new).toList();

            KafkaThemeTimeResponseDto responseDto = new KafkaThemeTimeResponseDto(request.getRequestId(), themeTimeResponseDtoList);
            handleThemeTimeResponse(responseDto);
        }catch (GlobalCustomException e){
            log.error(e.getMessage());
        }
    }

    private void handleThemeTimeResponse(KafkaThemeTimeResponseDto response) {
        CompletableFuture<List<ThemeTimeResponseDto>> future = responseThemeTimeFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            future.complete(response.getResponseDtoList());
        }
    }

}
