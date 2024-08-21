package com.sparta.domain.kakaopayment.service;


import com.sparta.domain.kakaopayment.dto.request.PaymentCreateRequestDto;
import com.sparta.domain.kakaopayment.dto.response.KakaoResponseDto;
import com.sparta.domain.kakaopayment.dto.response.PaymentResponseDto;
import com.sparta.domain.payment.entity.Payment;
import com.sparta.domain.payment.repository.PaymentRepository;
import com.sparta.domain.reservation.entity.PaymentStatus;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.entity.ReservationStatus;
import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.entity.ThemeTimeStatus;
import com.sparta.global.exception.customException.PaymentException;
import com.sparta.global.exception.customException.ReservationException;
import com.sparta.global.exception.errorCode.PaymentErrorCode;
import com.sparta.global.exception.errorCode.ReservationErrorCode;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final ReservationRepository reservationRepository;
  private final PaymentRepository paymentRepository;

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


  // 단건결제
  private static final String KAKAO_PAY_API_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
  // 주문조회
  private static final String KAKAO_ORDER_API_URL = "https://open-api.kakaopay.com/online/v1/payment/order";
  // 결제취소
  private static final String KAKAO_CANCEL_API_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";


  /**
   * 카카오 페이 결제진행할 url
   *
   * @param reservationId 예약 id
   * @return KakaoResponseDto 결제고유번호(tid) , 결제진행할 url(next_redirect_pc_url)
   */
  public KakaoResponseDto preparePayment(Long reservationId) {

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
    params.put("cancel_url", cancelURL + "/" + this.reservationId);
    params.put("fail_url", failURL + "/" + this.reservationId);

    HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_PAY_API_URL, entity, Map.class);

    return new KakaoResponseDto(response.getBody().get("tid").toString(),
        response.getBody().get("next_redirect_pc_url").toString());
  }

  /**
   * 카카오 페이 결제완료시 payment에 데이터 저장
   *
   * @param requestDto : reservationId - 예약 id, tid - 결제고유번호
   * @return PaymentResponseDto 완료된 결제 정보
   */
  @Transactional
  public PaymentResponseDto kakaoPaySuccess(PaymentCreateRequestDto requestDto) {

    Reservation reservation = reservationRepository.findByIdOrElse(requestDto.getReservationId());

    Payment payment = Payment.builder()
        .tid(requestDto.getTid())
        .cid(cid)
        .reservation(reservation)
        .build();

    if (reservation.getReservationStatus() == ReservationStatus.COMPLETE) {
      throw new ReservationException(ReservationErrorCode.RESERVATION_DUPLICATION);
    }

    reservation.updateReservationStatus(); // change Reservation COMPLETE
    reservation.getThemeTime().updateThemeTimeStatus(ThemeTimeStatus.DISABLE);
    paymentRepository.save(payment);

    return new PaymentResponseDto(payment);
  }

  /**
   * 카카오 페이 환불
   *
   * @param reservationId 예약 id
   */
  @Transactional
  public void refundPayment(Long reservationId) {

    Payment payment = paymentRepository.findByReservationId(reservationId);

    if (payment.getPaymentStatus() != PaymentStatus.COMPLETE) {
      throw new PaymentException(PaymentErrorCode.ALREAY_REFUND);
    }
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "SECRET_KEY " + kakaoApiKey);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("cid", cid);
//        params.put("tid", payment.getTid());
//        params.put("cancel_amount", String.valueOf(payment.getReservation().getPrice()));
//        params.put("cancel_tax_free_amount", "0");
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(KAKAO_CANCEL_API_URL, entity, Map.class);

    Reservation reservation = payment.getReservation();

    reservation.cancelReservation();
    payment.refundPayment();
  }

}