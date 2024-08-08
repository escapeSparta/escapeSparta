package com.sparta.kafkaError;

import com.sparta.global.exception.customException.GlobalCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Component
@Slf4j
public class KafkaErrorHandler {

    public DefaultErrorHandler customErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, e) -> {
            log.error("[Error] topic = {}, key = {}, value = {}, error message = {}", consumerRecord.topic(),
                    consumerRecord.key(), consumerRecord.value(), e.getMessage());
        }, new FixedBackOff(1000L, 3)); // 1초 간격으로 최대 3번
        errorHandler.addNotRetryableExceptions(GlobalCustomException.class); // retry X

        return errorHandler;
    }
}