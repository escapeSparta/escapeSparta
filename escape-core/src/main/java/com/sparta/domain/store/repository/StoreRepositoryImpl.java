package com.sparta.domain.store.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.domain.payment.entity.QPayment;
import com.sparta.domain.reservation.entity.PaymentStatus;
import com.sparta.domain.reservation.entity.QReservation;
import com.sparta.domain.store.entity.QStore;
import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.domain.store.entity.StoreStatus;
import com.sparta.domain.theme.entity.QTheme;
import com.sparta.global.exception.customException.StoreException;
import com.sparta.global.exception.errorCode.StoreErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StoreRepositoryImpl implements StoreRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Store> findByName(String name, StoreRegion storeRegion, Pageable pageable) {
        QStore store = QStore.store;

        JPAQuery<Store> query = jpaQueryFactory.selectFrom(store)
                .where(nameContains(name))
                .where(storeRegionContains(storeRegion))
                .where(store.storeStatus.eq(StoreStatus.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order order : pageable.getSort()) {
            PathBuilder<Store> pathBuilder = new PathBuilder<>(store.getType(), store.getMetadata());
            query.orderBy(new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty(), Comparable.class)
            ));
        }

        JPAQuery<Long> total = jpaQueryFactory.select(store.count())
                .from(store)
                .where(nameContains(name))
                .where(storeRegionContains(storeRegion))
                .where(store.storeStatus.eq(StoreStatus.ACTIVE));

        List<Store> results = query.fetch();

        //total 계산 쿼리를 지연로딩한다.
        return PageableExecutionUtils.getPage(results, pageable, () -> Optional.ofNullable(total.fetchOne()).orElse(0L));
//        return new PageImpl<>(results, pageable, Optional.ofNullable(total.fetchOne()).orElse(0L));
    }

    @Override
    public Store findByActiveStore(Long storeId) {
        QStore store = QStore.store;

        JPAQuery<Store> query = jpaQueryFactory.selectFrom(store)
                .where(store.storeStatus.eq(StoreStatus.ACTIVE)
                        .and(store.id.eq(storeId)));


        return Optional.ofNullable(query.fetchFirst()).orElseThrow(() ->
                new StoreException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private BooleanExpression nameContains(String name) {
        QStore store = QStore.store;
        return name != null ? store.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression storeRegionContains(StoreRegion storeRegion) {
        QStore store = QStore.store;
        return storeRegion == StoreRegion.ALL ? null : store.storeRegion.eq(storeRegion);
    }

    @Override
    public List<Store> findTopStore() {
        QStore store = QStore.store;
        QTheme theme = QTheme.theme;
        QReservation reservation = QReservation.reservation;
        QPayment payment = QPayment.payment;

        return jpaQueryFactory.select(store)
                .from(store)
                .leftJoin(theme).on(theme.store.eq(store)).fetchJoin()
                .leftJoin(reservation).on(reservation.theme.eq(theme)).fetchJoin()
                .leftJoin(payment).on(payment.reservation.eq(reservation))
                .where(store.storeStatus.eq(StoreStatus.ACTIVE)
                        .and(payment.paymentStatus.eq(PaymentStatus.COMPLETE)))
                .groupBy(store.id)
                .orderBy(reservation.count().desc())
                .limit(10)
                .fetch();
    }
}
