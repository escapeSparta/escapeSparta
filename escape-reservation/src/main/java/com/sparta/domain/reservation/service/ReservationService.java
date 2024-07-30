package com.sparta.domain.reservation.service;

import com.sparta.domain.kakaopayment.service.PaymentService;
import com.sparta.domain.reservation.dto.ReservationCreateRequestDto;
import com.sparta.domain.reservation.dto.ReservationCreateResponseDto;
import com.sparta.domain.reservation.dto.ReservationResponseDto;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.entity.ReservationStatus;
import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.repository.ThemeTimeRepository;
import com.sparta.domain.user.entity.User;
import com.sparta.global.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ThemeTimeRepository themeTimeRepository;
    private final PaymentService paymentService;


    /**
     * 예약 생성
     * @param requestDto 예약 생성에 필요한 데이터
     * @param user 로그인 유저
     * @return 예약
     */
    @Transactional
    @DistributedLock(key = "#requestDto.getThemeTimeId")
    public ReservationCreateResponseDto createReservation(ReservationCreateRequestDto requestDto, User user) {
        ThemeTime themeTime = themeTimeRepository.checkStoreAndThemeActive(requestDto.getThemeTimeId());
        reservationRepository.checkReservation(themeTime);

        Reservation reservation = Reservation.builder()
                .player(requestDto.getPlayer())
                .price(requestDto.getPrice())
                .paymentStatus(requestDto.getPaymentStatus())
                .reservationStatus(ReservationStatus.ACTIVE)
                .user(user)
                .theme(themeTime.getTheme())
                .themeTime(themeTime)
                .build();

        return new ReservationCreateResponseDto( reservationRepository.save(reservation));
    }

    /**
     * 예약 취소
     * @param reservationId 취소할 에약 id
     * @param user 로그인 유저
     */
    @Transactional
    public void deleteReservation(Long reservationId, User user) {
        Reservation reservation = reservationRepository.findByIdAndUserAndActive(reservationId, user);
        reservation.updateReservationStatus();

        paymentService.refundPayment(reservationId);
    }

    /**
     * 예약 내역 조회
     *
     * @param user 로그인 유저
     * @return 예약 내역
     */
    public List<ReservationResponseDto> getReservations(User user) {
        List<Reservation> reservationList = reservationRepository.findByUser(user);

        return reservationList.stream().map(ReservationResponseDto::new).toList();
    }
}
