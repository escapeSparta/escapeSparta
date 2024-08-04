package com.sparta.domain.kakaopayment.service;


import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final ReservationRepository reservationRepository;

    public Long reservationId;
    @Value("${kakao-payment.admin-key}")
    private String kakaoApiKey; //kakao api key
    @Value("${kakao-payment.cid}")
    private String cid; // 가맹점 코드
    @Value("${kakao-payment.approve-url}")
    private String approveURL; //결제창 넘겨주기
    @Value("${kakao-payment.cancel-url}")
    private String cancelURL; // 결제실패
    @Value("${kakao-payment.fail-url}")
    private String failURL; //결제완료


    //단건결제
    private static final String KAKAO_PAY_API_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    //주문조회
    private static final String KAKAO_ORDER_API_URL = "https://open-api.kakaopay.com/online/v1/payment/order";
    //결제취소
    private static final String KAKAO_CANCEL_API_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";


    /**
     * TODO: 결제할 수 있는 URL 넘겨줌
     *
     * @param reservationId 예약 아이디
     * @return key - value format json
     * @author SEMI
     */
    public Map<String, Object> preparePayment(Long reservationId) {

        Reservation reservation = reservationRepository.findByIdOrElse(reservationId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoApiKey);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("partner_order_id", String.valueOf(reservationId));
        params.put("partner_user_id", String.valueOf(reservation.getUser().getId()));

        String itemName = reservation.getTheme().getTitle() + " / " +
                reservation.getPlayer() + "인 / " +
                reservation.getThemeTime().getStartTime();

        params.put("item_name", itemName);
        params.put("quantity", "1"); //1개의 예약은 1개의 수량 고정
        params.put("total_amount", String.valueOf(reservation.getPrice()));
        params.put("vat_amount", "0");
        params.put("tax_free_amount", "0");
        params.put("approval_url", approveURL);
        params.put("cancel_url", cancelURL+"/"+this.reservationId);
        params.put("fail_url", failURL+"/"+this.reservationId);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_PAY_API_URL, entity, Map.class);

        reservation.paymentToReservation(cid,
                                        Objects.requireNonNull(response.getBody()).get("tid").toString());

        return response.getBody();
    }

    /**
     * TODO: 결제 환불
     *
     * @param reservationId 예약 고유 id
     * @return key - value format json
     * @author SEMI
     */
    @Transactional
    public Map<String, Object> refundPayment(Long reservationId) {

        Reservation reservation = reservationRepository.findByIdOrElse(reservationId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoApiKey);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", reservation.getTid());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        reservation.updateReservationStatus();
        ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_ORDER_API_URL, entity, Map.class);


        return response.getBody();
    }



    /**
     * TODO: 결제정보 조회
     *
     * @param reservationId 결제정보
     * @return key - value format json
     * @author SEMI
     */
    public Map<String, Object> getPaymentInfo(Long reservationId) {

        Reservation reservation = reservationRepository.findByIdOrElse(reservationId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoApiKey);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", reservation.getTid());
        params.put("cancel_amount", String.valueOf(reservation.getPrice()));
        params.put("cancel_tax_free_amount", "0");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_CANCEL_API_URL, entity, Map.class);

        reservation.updateReservationStatus();
        reservation.paymentToReservation(cid,
                Objects.requireNonNull(response.getBody()).get("tid").toString());

        return response.getBody();
    }
}