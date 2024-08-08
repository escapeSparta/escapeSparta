package com.sparta.domain.reservation.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.domain.reservation.entity.QReservation;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.entity.ReservationStatus;
import com.sparta.domain.store.entity.QStore;
import com.sparta.domain.theme.entity.QTheme;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReservationRepositoryImpl implements ReservationRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Reservation findByThemeTime(ThemeTime themeTime) {
        QReservation reservation = QReservation.reservation;

        JPAQuery<Reservation> query = jpaQueryFactory.selectFrom(reservation)
                .where(reservation.themeTime.eq(themeTime),
                        reservation.reservationStatus.eq(ReservationStatus.ACTIVE));

        return query.fetchFirst();
    }

    @Override
    public Reservation findByIdAndActive(Long reservationId, User user) {
        QReservation reservation = QReservation.reservation;

        JPAQuery<Reservation> query = jpaQueryFactory.selectFrom(reservation)
                .where(reservation.id.eq(reservationId),
                        reservation.reservationStatus.eq(ReservationStatus.ACTIVE),
                        reservation.user.eq(user));

        return query.fetchFirst();
    }

    @Override
    public List<Reservation> findByUser(User user) {
        QReservation reservation = QReservation.reservation;
        QTheme theme = QTheme.theme;
        QStore store = QStore.store;

        JPAQuery<Reservation> query = jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.theme, theme).fetchJoin()
                .leftJoin(theme.store, store).fetchJoin()
                .where(reservation.user.eq(user))
                .orderBy(reservation.themeTime.startTime.desc());

        return query.fetch();
    }

    @Override
    public List<Reservation> findByTheme(Theme theme) {
        QReservation reservation = QReservation.reservation;
        QTheme qTheme = QTheme.theme;
        QStore store = QStore.store;

        return jpaQueryFactory.selectFrom(reservation)
                .leftJoin(reservation.theme, qTheme).fetchJoin()
                .leftJoin(reservation.theme.store, store).fetchJoin()
                .where(reservation.theme.eq(theme))
                .fetch();
    }
}
