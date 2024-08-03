package com.sparta.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReservationCreateRequestDto {
    private String requestId;
    private ReservationCreateRequestDto requestDto;
    private Long userId;
}
