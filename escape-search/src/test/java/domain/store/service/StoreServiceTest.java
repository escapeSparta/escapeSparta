//package domain.store.service;
//
//import com.sparta.domain.store.dto.KafkaStoreRequestDto;
//import com.sparta.domain.store.dto.StoreResponseDto;
//import com.sparta.domain.store.entity.StoreRegion;
//import com.sparta.domain.store.service.StoreService;
//import com.sparta.global.kafka.KafkaTopic;
//import org.aspectj.lang.annotation.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class StoreServiceTest {
//
//    @Mock
//    private KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;
//
//    @InjectMocks
//    private StoreService storeService;
//
//    private ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;
//
//    @BeforeEach
//    public void setup() {
//        responseFutures = new ConcurrentHashMap<>();
//        storeService = new StoreService(kafkaTemplate, responseFutures);
//    }
//
//    @Test
//    public void 카페_조회_성공(){
//        // given
//        int pageNum = 1;
//        int pageSize = 10;
//        boolean isDesc = false;
//        String keyWord = "";
//        StoreRegion storeRegion = StoreRegion.ALL;
//        String sort = "name";
//        String requestId = UUID.randomUUID().toString();
//        KafkaStoreRequestDto requestDto = new KafkaStoreRequestDto(requestId, pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
//
//        when(
//                storeService.sendKafka(
//                        anyString(), any(KafkaStoreRequestDto.class)
//                )
//        ).thenReturn(result);
//
//        CompletableFuture<SendResult<String, KafkaStoreRequestDto>> result2 = storeService.sendKafka(
//                "store_request_topic", requestDto
//        );
//
//        assertThat(result2).isEqualTo(result);
//    }
////
////    @Test
////    public void setValue() {
////        값을 세팅하는 것만 테스트
////    }
////
////    @Test
////    public void sendKafka() {
////        카프카에 보내는것만 테스트
////    }
////
////    @Test
////    public void getFromKafka() {
////        카프카에서 값을 가져오는 것만 테스트
////    }
//}
