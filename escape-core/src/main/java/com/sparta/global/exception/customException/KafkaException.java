package com.sparta.global.exception.customException;

import com.sparta.global.exception.errorCode.ErrorCode;

public class KafkaException extends GlobalCustomException{
    public KafkaException(ErrorCode errorCode) {
        super(errorCode);
    }
}
