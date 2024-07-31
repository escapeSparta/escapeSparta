package com.sparta.domain.reservation.repository;

import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.entity.ReservationStatus;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.user.entity.User;
import com.sparta.global.exception.customException.ReservationException;
import com.sparta.global.exception.errorCode.ReservationErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom{

    default Reservation findByIdOrElse(Long reservationId){
       return findById(reservationId).orElseThrow(
               ()->new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }


    Optional<Reservation> findByIdAndUser(Long reservationId, User user);

    Optional<Reservation> findByIdAndReservationStatus(Long reservationId, ReservationStatus reservationStatus);

    default Reservation findByIdAndUserOrElseThrow(Long reservationId, User user) {
        return findByIdAndUser(reservationId, user).orElseThrow(() ->
                new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    default void checkReservationDuplicated(ThemeTime themeTime){
        if(findByThemeTime(themeTime) != null){
            throw new ReservationException(ReservationErrorCode.RESERVATION_DUPLICATION);
        }
    }

    default void checkReservation(ThemeTime themeTime){
        if(findByThemeTime(themeTime) != null){
            throw new ReservationException(ReservationErrorCode.RESERVATION_DUPLICATION);
        }
    }

    default Reservation findByIdAndUserAndActive(Long reservationId, User user){
        Reservation reservation = findByIdAndActive(reservationId, user);
        if(reservation == null){
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
        return reservation;
    }

    default Reservation findActiveReservation(Long reservationId) {
        return findByIdAndReservationStatus(reservationId, ReservationStatus.ACTIVE).orElseThrow(() ->
                new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }
}
