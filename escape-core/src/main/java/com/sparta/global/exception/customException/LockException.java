package com.sparta.global.exception.customException;

import com.sparta.global.exception.errorCode.ErrorCode;

public class LockException extends GlobalCustomException{
    public LockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
