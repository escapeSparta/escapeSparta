package com.sparta.domain.reservation.service;

import com.sparta.domain.reservation.dto.*;
import com.sparta.domain.reservation.emailService.KafkaEmailProducer;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationRequestService {

    private final KafkaTemplate<String, KafkaReservationCreateResponseDto> kafkaReservationCreateTemplate;
    private final KafkaTemplate<String, KafkaReservationGetResponseDto> kafkaReservationGetTemplate;
    private final KafkaTemplate<String, GlobalCustomException> kafkaErrorTemplate;

    private final ReservationRepository reservationRepository;
    private final ThemeTimeRepository themeTimeRepository;
    private final UserRepository userRepository;
    private final KafkaEmailProducer kafkaEmailProducer;

    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_CREATE_REQUEST_TOPIC, groupId = "${GROUP_ID}")
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

            kafkaReservationCreateTemplate.send(KafkaTopic.RESERVATION_CREATE_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
//            kafkaErrorTemplate.send(KafkaTopic.ERROR_TOPIC, e);
        }
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_DELETE_REQUEST_TOPIC, groupId = "${GROUP_ID}")
    public void handleDeleteReservationRequest(KafkaReservationDeleteRequestDto requestDto) {
        try {
            User user = userRepository.findByIdOrElseThrow(requestDto.getUserId());
            Reservation reservation = reservationRepository.findByIdAndUserAndActive(requestDto.getReservationId(), user);

//        paymentService.refundPayment(requestDto.getReservationId());
            reservation.updateReservationStatus();
            ThemeTime themeTime = reservation.getThemeTime();
            themeTime.updateThemeTimeStatus();
            kafkaEmailProducer.sendDeleteReservationEmail(KafkaTopic.PAYMENT_DELETE_TOPIC, user.getEmail());
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.RESERVATION_GET_REQUEST_TOPIC, groupId = "${GROUP_ID}")
    public void handleGetReservationRequest(KafkaReservationGetRequestDto requestDto) {
        try {
            User user = userRepository.findByIdOrElseThrow(requestDto.getUserId());
            List<Reservation> reservationList = reservationRepository.findByUser(user);
            List<ReservationResponseDto> responseDtoList = reservationList.stream().map(ReservationResponseDto::new).toList();
            KafkaReservationGetResponseDto responseDto = new KafkaReservationGetResponseDto(requestDto.getRequestId(), responseDtoList);
            kafkaReservationGetTemplate.send(KafkaTopic.RESERVATION_GET_RESPONSE_TOPIC, responseDto);
        }catch (GlobalCustomException e){
            log.error("GlobalCustomException 에러 발생: {}", e.getMessage());
        }
    }

}
