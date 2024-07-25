package com.sparta.controller;

import com.sparta.feign.EscapeReservationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConsumerController {

    private final EscapeReservationClient escapeReservationClient;

    @GetMapping("/reservations")
    public String getReservation(){
        return escapeReservationClient.getReservation();
    }
}
