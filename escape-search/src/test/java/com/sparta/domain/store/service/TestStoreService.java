package com.sparta.domain.store.service;

import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.StoreRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestStoreService {

    @Mock
    private KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;

    @InjectMocks
    private StoreService storeService;

    @Mock
    private ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;

    @BeforeEach
    public void setup() {
        // @InjectMocks가 자동으로 주입하도록 하여 수동 초기화를 제거합니다.
    }

    @Test
    public void 카페_조회_성공(){
        // given
        int pageNum = 1;
        int pageSize = 10;
        boolean isDesc = false;
        String keyWord = "";
        StoreRegion storeRegion = StoreRegion.ALL;
        String sort = "name";
        String requestId = UUID.randomUUID().toString();

        CompletableFuture<Page<StoreResponseDto>> future = new CompletableFuture<>();
        responseFutures.put(requestId, future);
        Page<StoreResponseDto> testPage = new PageImpl<>(new ArrayList<>());
        future.complete(testPage);

        when(responseFutures.get(requestId)).thenReturn(future);
//        when(testService.getStores(pageNum, pageSize, isDesc, keyWord, storeRegion, sort)).thenReturn(testPage);

        // when
        Page<StoreResponseDto> result = storeService.getStores(pageNum, pageSize, isDesc, keyWord, storeRegion, sort);

        // then
        assertThat(result).isEqualTo(future);

//        KafkaStoreRequestDto requestDto = new KafkaStoreRequestDto(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
//        CompletableFuture<SendResult<String, KafkaStoreRequestDto>> result2 = new CompletableFuture<>();
//
//        //when
//        when(testService.sendKafka(anyString(), any())).thenReturn(result2);
//        CompletableFuture<SendResult<String, KafkaStoreRequestDto>> result3 = testService.sendKafka(KafkaTopic.STORE_REQUEST_TOPIC, requestDto);
//
//        //then
//        assertThat(result2).isEqualTo(result3);
    }

//    @Test
//    public void setValue() {
//        값을 세팅하는 것만 테스트
//    }
//
//    @Test
//    public void sendKafka() {
//        카프카에 보내는것만 테스트
//    }
//
//    @Test
//    public void getFromKafka() {
//        카프카에서 값을 가져오는 것만 테스트
//    }
}
