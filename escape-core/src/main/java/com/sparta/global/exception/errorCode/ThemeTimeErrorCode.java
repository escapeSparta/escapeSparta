package com.sparta.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ThemeTimeErrorCode implements ErrorCode{
    THEME_TIME_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "테마 예약 시간대를 찾을 수 없습니다."),
    INVALID_START_AND_END_TIME(HttpStatus.BAD_REQUEST.value(), "종료 시간이 시작 시간보다 빠를 수 없습니다.");

    private final int httpStatusCode;
    private final String errorDescription;
}
