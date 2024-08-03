package com.sparta.domain.store.dto;

import com.sparta.domain.store.entity.StoreRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaStoreRequestDto {
    private String requestId;
    private int pageNum;
    private int pageSize;
    private boolean isDesc;
    private String keyWord;
    private StoreRegion storeRegion;
    private String sort;
}
