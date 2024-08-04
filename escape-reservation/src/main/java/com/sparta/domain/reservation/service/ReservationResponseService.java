package com.sparta.domain.reservation.service;

import com.sparta.domain.kafka.kafkaEmailService.KafkaEmailProducer;
import com.sparta.domain.reservation.dto.KafkaReservationCreateResponseDto;
import com.sparta.domain.reservation.dto.KafkaReservationGetResponseDto;
import com.sparta.domain.reservation.dto.ReservationCreateResponseDto;
import com.sparta.domain.reservation.dto.ReservationResponseDto;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationResponseService {

    private final KafkaEmailProducer kafkaEmailProducer;

    @KafkaListener(topics = KafkaTopic.RESERVATION_CREATE_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleReservationCreateResponse(KafkaReservationCreateResponseDto response) {
        CompletableFuture<ReservationCreateResponseDto> future = ReservationService.responseCreateFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            kafkaEmailProducer.sendCreateReservationEmail(KafkaTopic.PAYMENT_TOPIC, response.getEmail());
            future.complete(response.getResponseDto());
        }
    }

    @KafkaListener(topics = KafkaTopic.RESERVATION_GET_RESPONSE_TOPIC, groupId = "${GROUP_ID}")
    public void handleReservationGetResponse(KafkaReservationGetResponseDto response) {
        CompletableFuture<List<ReservationResponseDto>> future = ReservationService.responseGetFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            future.complete(response.getResponseDtoList());
        }
    }

}
