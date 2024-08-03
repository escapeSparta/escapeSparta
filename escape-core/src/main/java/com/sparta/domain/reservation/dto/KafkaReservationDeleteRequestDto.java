package com.sparta.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReservationDeleteRequestDto {
    private String requestId;
    private Long reservationId;
    private Long userId;
}
