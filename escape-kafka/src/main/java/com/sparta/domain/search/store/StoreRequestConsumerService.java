package com.sparta.domain.search.store;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRequestConsumerService {
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = KafkaTopic.STORE_REQUEST_TOPIC, groupId = "${GROUP_SEARCH_ID}")
    public void handleStoreRequest(KafkaStoreRequestDto request) {
        try {
            Pageable pageable = PageUtil.createPageable(request.getPageNum(), request.getPageSize(), request.isDesc(), request.getSort());
            Page<Store> stores = storeRepository.findByName(request.getKeyWord(), request.getStoreRegion(), pageable);
            Page<StoreResponseDto> storeResponseDtoPage = stores.map(StoreResponseDto::new);
            KafkaStoreResponseDto response = new KafkaStoreResponseDto(request.getRequestId(), storeResponseDtoPage);

            try {
                String message = objectMapper.writeValueAsString(response);
                log.error("2222");
                kafkaTemplate.send(KafkaTopic.STORE_RESPONSE_TOPIC, message);
            } catch (Exception e) {
                log.error("직열화 에러: {}", e.getMessage());
            }
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

}
