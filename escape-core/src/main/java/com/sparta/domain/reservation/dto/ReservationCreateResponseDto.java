package com.sparta.domain.reservation.dto;

import com.sparta.domain.reservation.entity.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationCreateResponseDto {
    private Long reservationId;
    private String storeName; //가게이름
    private String storeAddress; //가게지역
    private String themeTitle; //테마이름
    private Integer level;   //난이도
    private LocalDateTime startTime;   //시작시간
    private Integer player;
    private Integer duration;
    private String image;
    private Long totalPrice;
    private LocalDateTime createAt;

    public ReservationCreateResponseDto(Reservation reservation){
        reservationId = reservation.getId();
        storeName = reservation.getTheme().getStore().getName();
        storeAddress = reservation.getTheme().getStore().getAddress();
        themeTitle = reservation.getTheme().getTitle();
        level = reservation.getTheme().getLevel();
        startTime = reservation.getThemeTime().getStartTime();
        player = reservation.getPlayer();
        duration = reservation.getTheme().getDuration();
        image = reservation.getTheme().getThemeImage();
        totalPrice = reservation.getPrice();
        createAt = reservation.getCreatedAt();
    }
}
