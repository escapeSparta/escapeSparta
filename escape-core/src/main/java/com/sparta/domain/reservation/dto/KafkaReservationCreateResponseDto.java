package com.sparta.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReservationCreateResponseDto {
    private String requestId;
    private ReservationCreateResponseDto responseDto;
    private String email;
}
