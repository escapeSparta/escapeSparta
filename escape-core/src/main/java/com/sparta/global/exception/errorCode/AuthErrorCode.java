package com.sparta.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode{
    NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST.value(), "아직 만료되지 않은 액세스 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(),"해당 사용자의 리프레쉬 토큰이 존재하지 않습니다, "),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 리프레시 토큰입니다. 재로그인이 필요합니다.");

    private final int httpStatusCode;
    private final String errorDescription;
}
