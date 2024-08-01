package com.sparta.global.lock;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AopForTransaction {

    @Transactional(value = Transactional.TxType.REQUIRES_NEW) // 항상 새로운 트랜잭션 내에서 실행되도록 설정
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("로직 실행 중");
        return joinPoint.proceed();
    }
}
