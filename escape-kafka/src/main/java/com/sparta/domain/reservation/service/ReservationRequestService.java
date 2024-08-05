package com.sparta.domain.reservation.service;

import com.sparta.domain.reservation.dto.*;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.entity.ReservationStatus;
import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.entity.ThemeTimeStatus;
import com.sparta.domain.theme.repository.ThemeTimeRepository;
import com.sparta.domain.user.entity.User;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.global.exception.customException.GlobalCustomException;
import com.sparta.global.exception.customException.ReservationException;
import com.sparta.global.exception.errorCode.ReservationErrorCode;
import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationRequestService {

    private final KafkaTemplate<String, KafkaReservationCreateResponseDto> kafkaReservationCreateTemplate;
    private final KafkaTemplate<String, KafkaReservationGetResponseDto> kafkaReservationGetTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<ReservationCreateResponseDto>> responseCreateFutures;
    private final ConcurrentHashMap<String, CompletableFuture<List<ReservationResponseDto>>> responseGetFutures;

    private final ReservationRepository reservationRepository;
    private final ThemeTimeRepository themeTimeRepository;
    private final UserRepository userRepository;
    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_CREATE_REQUEST_TOPIC, groupId = "${GROUP_RESERVATION_ID}")
    public void handleCreateReservationRequest(KafkaReservationCreateRequestDto requestDto) {
        try {
            User user = userRepository.findByIdOrElseThrow(requestDto.getUserId());
            ThemeTime themeTime = themeTimeRepository.checkStoreAndThemeActive(requestDto.getRequestDto().getThemeTimeId());
            reservationRepository.checkReservation(themeTime);
            if (themeTime.getThemeTimeStatus() == ThemeTimeStatus.DISABLE) {
                throw new ReservationException(ReservationErrorCode.RESERVATION_DUPLICATION);
            }

            Reservation reservation = Reservation.builder()
                    .player(requestDto.getRequestDto().getPlayer())
                    .price(requestDto.getRequestDto().getPrice())
                    .paymentStatus(requestDto.getRequestDto().getPaymentStatus())
                    .reservationStatus(ReservationStatus.ACTIVE)
                    .user(user)
                    .theme(themeTime.getTheme())
                    .themeTime(themeTime)
                    .build();

            themeTime.updateThemeTimeStatus();

            KafkaReservationCreateResponseDto responseDto = new KafkaReservationCreateResponseDto(requestDto.getRequestId()
                    , new ReservationCreateResponseDto(reservationRepository.save(reservation)), user.getEmail());
            handleReservationCreateResponse(responseDto);
//            kafkaReservationCreateTemplate.send(KafkaTopic.RESERVATION_CREATE_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    private void handleReservationCreateResponse(KafkaReservationCreateResponseDto response) {
        CompletableFuture<ReservationCreateResponseDto> future = responseCreateFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            future.complete(response.getResponseDto());
        }
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_DELETE_REQUEST_TOPIC, groupId = "${GROUP_RESERVATION_ID}")
    public void handleDeleteReservationRequest(KafkaReservationDeleteRequestDto requestDto) {
        try {
            User user = userRepository.findByIdOrElseThrow(requestDto.getUserId());
            Reservation reservation = reservationRepository.findByIdAndUserAndActive(requestDto.getReservationId(), user);

//        paymentService.refundPayment(requestDto.getReservationId());
            reservation.updateReservationStatus();
            ThemeTime themeTime = reservation.getThemeTime();
            themeTime.updateThemeTimeStatus();
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_GET_REQUEST_TOPIC, groupId = "${GROUP_RESERVATION_ID}")
    public void handleGetReservationRequest(KafkaReservationGetRequestDto requestDto) {
        try {
            User user = userRepository.findByIdOrElseThrow(requestDto.getUserId());
            List<Reservation> reservationList = reservationRepository.findByUser(user);
            List<ReservationResponseDto> responseDtoList = reservationList.stream().map(ReservationResponseDto::new).toList();
            KafkaReservationGetResponseDto responseDto = new KafkaReservationGetResponseDto(requestDto.getRequestId(), responseDtoList);
            handleReservationGetResponse(responseDto);
//            kafkaReservationGetTemplate.send(KafkaTopic.RESERVATION_GET_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    private void handleReservationGetResponse(KafkaReservationGetResponseDto response) {
        CompletableFuture<List<ReservationResponseDto>> future = responseGetFutures.remove(Objects.requireNonNull(response).getRequestId());
        if (future != null) {
            future.complete(response.getResponseDtoList());
        }
    }

}
