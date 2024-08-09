package domain.store.service;

import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.domain.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@EmbeddedKafka
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;

    @Mock
    private ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStores_shouldSendKafkaMessageAndReturnResponse() throws ExecutionException, InterruptedException {
        // given
        CompletableFuture<Page<StoreResponseDto>> future = new CompletableFuture<>();
        Page<StoreResponseDto> testPage = new PageImpl<>(new ArrayList<>());

        when(responseFutures.put(anyString(), any())).thenReturn(null);
        when(responseFutures.get(anyString())).thenReturn(future);

        future.complete(testPage);

        // when
        Page<StoreResponseDto> result = storeService.getStores(1, 10, true, null, StoreRegion.ALL, null);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo(testPage);
        verify(responseFutures).put(anyString(), any());
    }

//    @Mock
//    private KafkaTemplate<String, KafkaStoreRequestDto> kafkaTemplate;
//
//    @Mock
//    private ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;
//
//    @InjectMocks
//    private StoreService storeService;
//
//    @BeforeEach
//    public void setup() {
////        storeService = new StoreService(kafkaTemplate, responseFutures);
//    }
//
//    @Test
//    public void 카페_조회_성공() throws ExecutionException, InterruptedException {
//        // given
//        int pageNum = 1;
//        int pageSize = 10;
//        boolean isDesc = false;
//        String keyWord = "";
//        StoreRegion storeRegion = StoreRegion.ALL;
//        String sort = "name";
//
//        CompletableFuture<Page<StoreResponseDto>> future = new CompletableFuture<>();
//        Page<StoreResponseDto> testPage = new PageImpl<>(new ArrayList<>());
//
//        CompletableFuture<SendResult<String, KafkaStoreRequestDto>> kafkaResult= new CompletableFuture<>();
////        when(responseFutures.put(anyString(), any())).thenReturn(future);
////        when(kafkaTemplate.send(anyString(), any())).thenReturn(kafkaResult);
////        when(future.get()).thenReturn(testPage);
//
//         Page<StoreResponseDto> result = storeService.getStores(pageNum, pageSize, isDesc, keyWord, storeRegion, sort);
//
//         assertThat(result).isEqualTo(testPage);
//    }





//
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
