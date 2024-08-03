package com.sparta.config;

import com.sparta.domain.reservation.dto.ReservationCreateResponseDto;
import com.sparta.domain.reservation.dto.ReservationResponseDto;
import com.sparta.domain.review.dto.ReviewResponseDto;
import com.sparta.domain.store.dto.StoreResponseDto;
import com.sparta.domain.theme.dto.ThemeInfoResponseDto;
import com.sparta.domain.theme.dto.ThemeResponseDto;
import com.sparta.domain.theme.dto.ThemeTimeResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ConcurrentHashMapConfig {

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<ReservationCreateResponseDto>> ReservationResponseCreateFutures() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<Void>> ReservationResponseDeleteFutures() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<List<ReservationResponseDto>>> ReservationResponseGetFutures() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<List<ReviewResponseDto>>> ReviewResponseFutures(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<Page<StoreResponseDto>>> StoreResponseFutures(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<Page<ThemeResponseDto>>> ThemeResponseFutures(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<ThemeInfoResponseDto>> ThemeInfoResponseFutures(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, CompletableFuture<List<ThemeTimeResponseDto>>> ThemeTimeResponseFutures(){
        return new ConcurrentHashMap<>();
    }
}
