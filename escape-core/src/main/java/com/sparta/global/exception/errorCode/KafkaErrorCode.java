package com.sparta.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KafkaErrorCode implements ErrorCode{
    KAFKA_SERVER_ERROR(HttpStatus.BAD_REQUEST.value(), "서버에 문제가 있습니다."),
    KAFKA_RESPONSE_ERROR(HttpStatus.BAD_REQUEST.value(), "서버에 문제가 있거나. 요청 데이터가 잘못되었습니다."),

    ;

    private final int httpStatusCode;
    private final String errorDescription;
}
