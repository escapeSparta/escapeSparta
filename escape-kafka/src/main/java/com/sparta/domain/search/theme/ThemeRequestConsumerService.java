package com.sparta.domain.search.theme;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeRequestConsumerService {

    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;
    private final ThemeTimeRepository themeTimeRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, KafkaThemeInfoResponseDto> kafkaThemeInfoTemplate;
    private final KafkaTemplate<String, KafkaThemeTimeResponseDto> kafkaThemeTimeTemplate;

    @KafkaListener(topics = KafkaTopic.THEME_REQUEST_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeRequest(KafkaThemeRequestDto request) {
        try {
            Store store = storeRepository.findByIdOrElseThrow(request.getStoreId());

            Pageable pageable = PageUtil.createPageable(request.getPageNum(), request.getPageSize(), request.isDesc(), request.getSort());
            Page<Theme> themes = themeRepository.findByStore(store, pageable);
            Page<ThemeResponseDto> themeResponseDtoPage = themes.map(ThemeResponseDto::new);

            KafkaThemeResponseDto responseDto = new KafkaThemeResponseDto(request.getRequestId(), themeResponseDtoPage);

            try {
                log.error("3333");
                String message = objectMapper.writeValueAsString(responseDto);
                kafkaTemplate.send(KafkaTopic.THEME_RESPONSE_TOPIC, message);
            } catch (Exception e) {
                log.error("직열화 에러: {}", e.getMessage());
            }
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_INFO_REQUEST_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeInfoRequest(KafkaThemeInfoRequestDto request) {
        try {
            storeRepository.findByActiveStore(request.getStoreId());
            Theme theme = themeRepository.findByActiveTheme(request.getThemeId());
            ThemeInfoResponseDto themeInfoResponseDto = new ThemeInfoResponseDto(theme);
            KafkaThemeInfoResponseDto responseDto = new KafkaThemeInfoResponseDto(request.getRequestId(), themeInfoResponseDto);
            log.error("4444");
            kafkaThemeInfoTemplate.send(KafkaTopic.THEME_INFO_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopic.THEME_TIME_REQUEST_TOPIC, groupId = "${GROUP_ID}")
    public void handleThemeTimeRequest(KafkaThemeTimeRequestDto request) {
        try {
            LocalDate day = LocalDateTimeUtil.parseDateStringToLocalDate(request.getDay());
            storeRepository.findByActiveStore(request.getStoreId());
            List<ThemeTime> themeTimeList = themeTimeRepository.findThemeTimesByDate(request.getThemeId(), day);
            List<ThemeTimeResponseDto> themeTimeResponseDtoList = themeTimeList.stream().map(ThemeTimeResponseDto::new).toList();

            KafkaThemeTimeResponseDto responseDto = new KafkaThemeTimeResponseDto(request.getRequestId(), themeTimeResponseDtoList);
            log.error("5555");
            kafkaThemeTimeTemplate.send(KafkaTopic.THEME_TIME_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

}
