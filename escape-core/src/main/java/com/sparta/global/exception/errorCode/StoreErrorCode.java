package com.sparta.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 방탈출 카페를 찾을 수 없습니다."),
    STORE_ALREADY_ACCEPT(HttpStatus.FORBIDDEN.value(), "이미 등록된 방탈출 카페입니다.");
    private final int httpStatusCode;
    private final String errorDescription;

}