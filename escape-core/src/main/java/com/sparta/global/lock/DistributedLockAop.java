package com.sparta.global.lock;

import com.sparta.global.exception.customException.LockException;
import com.sparta.global.exception.errorCode.LockErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "Lock:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.sparta.global.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        log.info("lock on [method : {}] [key : {}]", method, key);

        RLock rLock = redissonClient.getLock(key);
        String lockName = rLock.getName();

        try {
            boolean avaliable = rLock.tryLock(
                    distributedLock.waitTime(), // 락 획득을 위해 기다릴 최대 시간
                    distributedLock.leaseTime(), // 락 자동 해제 전까지 유지될 시간
                    distributedLock.timeUnit() // 시간 단위
            );

            if(!avaliable) {
                throw new LockException(LockErrorCode.LOCK_NOT_AVAILABLE);
            }

            return aopForTransaction.proceed(joinPoint);
        } catch(InterruptedException e) {
            throw new LockException(LockErrorCode.LOCK_INTERRUPTED_ERROR);
        } finally {
            try {
                rLock.unlock();
                log.info("Unlock Complete : Lock : {}", lockName);
            } catch (IllegalMonitorStateException e) {
                log.info("락이 이미 종료되었습니다.");
            }
        }
    }
}
