package com.sparta.domain.reservation.service;

import com.sparta.domain.reservation.dto.*;
import com.sparta.domain.user.entity.User;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    public final ConcurrentHashMap<String, CompletableFuture<ReservationCreateResponseDto>> responseCreateFutures;
    public final ConcurrentHashMap<String, CompletableFuture<Void>> responseDeleteFutures;
    public final ConcurrentHashMap<String, CompletableFuture<List<ReservationResponseDto>>> responseGetFutures;

    private final KafkaTemplate<String, KafkaReservationCreateRequestDto> kafkaReservationCreateTemplate;
    private final KafkaTemplate<String, KafkaReservationDeleteRequestDto> kafkaReservationDeleteTemplate;
    private final KafkaTemplate<String, KafkaReservationGetRequestDto> kafkaReservationGetTemplate;

    /**
     * 예약 생성
     * @param requestDto 예약 생성에 필요한 데이터
     * @param user 로그인 유저
     * @return 예약
     */
    @Transactional
    public ReservationCreateResponseDto createReservation(ReservationCreateRequestDto requestDto, User user) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<ReservationCreateResponseDto> future = new CompletableFuture<>();
        responseCreateFutures.put(requestId, future);
        sendReservationCreateRequest(requestId, requestDto, user.getId());

        // Kafka로 요청을 전송하고 응답을 비동기적으로 기다림
        try {
            return future.get(); // 응답을 기다림
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }
    }

    private void sendReservationCreateRequest(String requestId, ReservationCreateRequestDto requestDto, Long userId) {
        KafkaReservationCreateRequestDto createRequest = new KafkaReservationCreateRequestDto(requestId, requestDto, userId);
        kafkaReservationCreateTemplate.send(KafkaTopic.RESERVATION_CREATE_REQUEST_TOPIC, createRequest);
    }

    /**
     * 예약 취소
     * @param reservationId 취소할 에약 id
     * @param user 로그인 유저
     */
    @Transactional
    public void deleteReservation(Long reservationId, User user) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Void> future = new CompletableFuture<>();
        responseDeleteFutures.put(requestId, future);
        sendReservationDeleteRequest(requestId, reservationId, user.getId());
    }

    private void sendReservationDeleteRequest(String requestId, Long reservationId, Long userId) {
        KafkaReservationDeleteRequestDto createRequest = new KafkaReservationDeleteRequestDto(requestId, reservationId, userId);
        kafkaReservationDeleteTemplate.send(KafkaTopic.RESERVATION_DELETE_REQUEST_TOPIC, createRequest);
    }

    /**
     * 예약 내역 조회
     *
     * @param user 로그인 유저
     * @return 예약 내역
     */
    public List<ReservationResponseDto> getReservations(User user) {

        String requestId = UUID.randomUUID().toString();
        CompletableFuture<List<ReservationResponseDto>> future = new CompletableFuture<>();
        responseGetFutures.put(requestId, future);
        sendReservationGetRequest(requestId, user.getId());

        // Kafka로 요청을 전송하고 응답을 비동기적으로 기다림
        try {
            return future.get(); // 응답을 기다림
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("리뷰 response 실패", e);
        }
    }

    private void sendReservationGetRequest(String requestId, Long userId) {
        KafkaReservationGetRequestDto createRequest = new KafkaReservationGetRequestDto(requestId, userId);
        kafkaReservationGetTemplate.send(KafkaTopic.RESERVATION_GET_REQUEST_TOPIC, createRequest);
    }
}
