package com.sparta.domain.kafka.kafkaEmailService;

import com.sparta.global.kafka.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEmailConsumer {

    private final AmazonSESService emailService;

    @KafkaListener(topics = KafkaTopic.PAYMENT_TOPIC, groupId = "${GROUP_ID}")
    public void createReservationSendEmail(String message) {

        if (message != null) {
            emailService.sendEmail(message, "예약 확인", createEmailBody(message));
        }
    }

    @KafkaListener(topics = KafkaTopic.PAYMENT_DELETE_TOPIC, groupId = "${GROUP_ID}")
    public void deleteReservationSendEmail(String message) {

        if (message != null) {
            emailService.sendEmail(message, "예약 취소", deleteEmailBody(message));
        }
    }

    private String createEmailBody(String message) {
        return "<h1>예약 확인</h1>"
                + "<p>감사합니다. 예약이 확인되었습니다.</p>"
                + "<p>예약 ID: " + message + "</p>";
    }

    private String deleteEmailBody(String message) {
        return "<h1>예약 취소</h1>"
                + "<p>감사합니다. 예약이 취소었습니다.</p>"
                + "<p>예약 취소 ID: " + message + "</p>";
    }
}
