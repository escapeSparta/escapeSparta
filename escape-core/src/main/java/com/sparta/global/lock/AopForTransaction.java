package com.sparta.global.lock;

import jakarta.transaction.Transactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
public class AopForTransaction {

    @Transactional(value = Transactional.TxType.REQUIRES_NEW) // 항상 새로운 트랜잭션 내에서 실행되도록 설정
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
