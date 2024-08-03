package com.sparta.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReservationGetResponseDto {
    private String requestId;
    private List<ReservationResponseDto> responseDtoList;
}
