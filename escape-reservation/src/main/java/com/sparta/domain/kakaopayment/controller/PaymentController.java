package com.sparta.domain.kakaopayment.controller;


import com.sparta.domain.kakaopayment.service.PaymentService;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.security.UserDetailsImpl;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/reservations/{reservationId}")
    public String preparePayment(@PathVariable Long reservationId,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model) {
        Map<String, Object> response = paymentService.preparePayment(reservationId);
        model.addAttribute("nextRedirectPcUrl", response.get("next_redirect_pc_url"));
        return "" + response.get("next_redirect_pc_url");
    }
//
//    @GetMapping("/kakaoPaySuccess")
//    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken, Model model) {
//        String frontendHomeUrl = "http://localhost:5173/home";  // 프론트엔드 URL과 포트 번호
//        return "redirect: " + frontendHomeUrl;
//    }
//
//    @GetMapping("/kakaoPayCancel")
//    public String kakaoPayCancel() {
//        // 프론트엔드의 결제 취소 화면 URL로 리다이렉트
//        String frontendCancelUrl = "http://localhost:5173/cancel";  // 프론트엔드 URL과 포트 번호
//        return "redirect:" + frontendCancelUrl;
//    }
//
//    @GetMapping("/kakaoPayFail")
//    public String kakaoPayFail() {
//        // 프론트엔드의 결제 실패 화면 URL로 리다이렉트
//        String frontendFailUrl = "http://localhost:5173/fail";  // 프론트엔드 URL과 포트 번호
//        return "redirect:" + frontendFailUrl;
//    }

}