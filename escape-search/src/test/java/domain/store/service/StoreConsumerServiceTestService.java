package domain.store.service;

import com.sparta.domain.store.dto.KafkaStoreRequestDto;
import com.sparta.domain.store.dto.KafkaStoreResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.store.service.StoreConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreConsumerServiceTestService {
    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> responseFutures;

    @InjectMocks
    private StoreConsumerService storeConsumerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleStoreRequest() {
        KafkaStoreRequestDto requestDto = new KafkaStoreRequestDto("requestId", 0, 10, true, "keyword", StoreRegion.ALL, "name");
        List<Store> storeList = new ArrayList<>();
        Page<Store> storePage = new PageImpl<>(storeList);

        when(storeRepository.findByName(anyString(), any(StoreRegion.class), any(Pageable.class))).thenReturn(storePage);

        storeConsumerService.handleStoreRequest(requestDto);

        verify(storeRepository, times(1)).findByName(anyString(), any(StoreRegion.class), any(Pageable.class));
    }

    @Test
    public void testHandleStoreResponse() {
        KafkaStoreResponseDto responseDto = new KafkaStoreResponseDto("requestId", new PageImpl<>(new ArrayList<>()));
        CompletableFuture<Page<StoreResponseDto>> future = new CompletableFuture<>();
        future.complete(new PageImpl<>(new ArrayList<>()));

        when(responseFutures.remove(anyString())).thenReturn(future);

//        storeConsumerService.handleStoreResponse(responseDto);

        verify(responseFutures, times(1)).remove(anyString());
    }

}