package com.sparta.domain.reservation.dto;

import com.sparta.domain.reservation.entity.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCreateRequestDto {
    private Long themeTimeId;
    private Integer player;
    private Long price;
    private PaymentStatus paymentStatus;
}
