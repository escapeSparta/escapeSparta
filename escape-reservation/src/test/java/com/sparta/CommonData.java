package com.sparta;

import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.entity.StoreRegion;
import com.sparta.domain.store.entity.StoreStatus;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.entity.ThemeStatus;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.entity.ThemeType;
import com.sparta.domain.user.entity.OAuthProvider;
import com.sparta.domain.user.entity.User;
import com.sparta.domain.user.entity.UserStatus;
import com.sparta.domain.user.entity.UserType;

import java.time.LocalDateTime;

public interface CommonData {
    String COMMON_PASSWORD = "Test1234!";

    String CONSUMER1_EMAIL = "consumer1@gmail.com";
    String CONSUMER1_NAME = "일반 사용자1";

    String CONSUMER2_EMAIL = "consumer2@gmail.com";
    String CONSUMER2_NAME = "일반 사용자2";

    String MANAGER_EMAIL = "manager@gmail.com";
    String MANAGER_NAME = "매니저";

    String ADMIN_EMAIL = "admin@gmail.com";
    String ADMIN_NAME = "관리자";

    User consumer1 = new User(
            CONSUMER1_NAME,
            CONSUMER1_EMAIL,
            COMMON_PASSWORD,
            OAuthProvider.ORIGIN,
            UserType.USER,
            UserStatus.ACTIVE);

    User consumer2 = new User(
            CONSUMER2_NAME,
            CONSUMER2_EMAIL,
            COMMON_PASSWORD,
            OAuthProvider.ORIGIN,
            UserType.USER,
            UserStatus.ACTIVE);

    User manager = new User(
            MANAGER_NAME,
            MANAGER_EMAIL,
            COMMON_PASSWORD,
            OAuthProvider.ORIGIN,
            UserType.MANAGER,
            UserStatus.ACTIVE);

    User admin = new User(
            ADMIN_NAME,
            ADMIN_EMAIL,
            COMMON_PASSWORD,
            OAuthProvider.ORIGIN,
            UserType.ADMIN,
            UserStatus.ACTIVE);

    Store store = Store.builder()
            .name("testStore")
            .address("testAddress")
            .phoneNumber("02-091-2498")
            .workHours("9:00 ~ 22:00")
            .manager(manager)
            .storeRegion(StoreRegion.SEOUL)
            .storeStatus(StoreStatus.ACTIVE)
            .build();

    Theme theme = Theme.builder()
            .title("testTheme")
            .contents("testContents")
            .level(5)
            .duration(120)
            .minPlayer(2)
            .maxPlayer(5)
            .price(20000L)
            .themeType(ThemeType.ADVENTURE)
            .themeStatus(ThemeStatus.ACTIVE)
            .store(store)
            .build();

    LocalDateTime startTime = LocalDateTime.now().plusWeeks(1);
    LocalDateTime endTime = startTime.plusHours(2);

    ThemeTime themeTime1 = ThemeTime.builder()
            .startTime(startTime)
            .endTime(endTime)
            .theme(theme)
            .build();

    ThemeTime themeTime2 = ThemeTime.builder()
            .startTime(startTime)
            .endTime(endTime)
            .theme(theme)
            .build();
}
