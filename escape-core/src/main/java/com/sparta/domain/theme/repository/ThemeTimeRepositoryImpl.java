package com.sparta.domain.theme.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.domain.store.entity.QStore;
import com.sparta.domain.store.entity.StoreStatus;
import com.sparta.domain.theme.entity.QTheme;
import com.sparta.domain.theme.entity.QThemeTime;
import com.sparta.domain.theme.entity.ThemeStatus;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.global.exception.customException.ThemeTimeException;
import com.sparta.global.exception.errorCode.ThemeTimeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ThemeTimeRepositoryImpl implements ThemeTimeRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ThemeTime> findThemeTimesByDate(Long themeId, LocalDate date) {
        QThemeTime themeTime = QThemeTime.themeTime;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return jpaQueryFactory.selectFrom(themeTime)
                .where(themeTime.startTime.between(startOfDay, endOfDay)
                        .and(themeTime.theme.id.eq(themeId)))
                .orderBy(themeTime.startTime.asc())
                .fetch();
    }

    @Override
    public ThemeTime findThemeTimeOfActiveStore(Long themeTimeId) {
        QThemeTime themeTime = QThemeTime.themeTime;
        QTheme theme = QTheme.theme;
        QStore store = QStore.store;

        JPAQuery<ThemeTime> query = jpaQueryFactory.selectFrom(themeTime)
                .leftJoin(themeTime.theme, theme).fetchJoin()
                .leftJoin(theme.store, store).fetchJoin()
                .where(
                        themeTime.id.eq(themeTimeId),
                        store.storeStatus.eq(StoreStatus.ACTIVE)
                );

        return Optional.ofNullable(query.fetchFirst()).orElseThrow(() ->
                new ThemeTimeException(ThemeTimeErrorCode.THEME_TIME_NOT_FOUND));
    }

    @Override
    public ThemeTime checkStoreAndThemeActive(Long themeTimeId) {
        QThemeTime themeTime = QThemeTime.themeTime;
        QTheme theme = QTheme.theme;
        QStore store = QStore.store;

        JPAQuery<ThemeTime> query = jpaQueryFactory.selectFrom(themeTime)
                .leftJoin(themeTime.theme, theme).fetchJoin()
                .leftJoin(theme.store, store).fetchJoin()
                .where(
                        themeTime.id.eq(themeTimeId),
                        store.storeStatus.eq(StoreStatus.ACTIVE),
                        theme.themeStatus.eq(ThemeStatus.ACTIVE)
                );

        return Optional.ofNullable(query.fetchFirst()).orElseThrow(() ->
                new ThemeTimeException(ThemeTimeErrorCode.THEME_TIME_NOT_FOUND));
    }
}
