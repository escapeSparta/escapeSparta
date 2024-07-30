package com.sparta.global.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 락 이름
     */
    String key();

    /**
     * 락 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 획득을 기다리는 시간
     */
    long waitTime() default 5L;

    /**
     * 락 점유 시간
     */
    long leaseTime() default 10L;
}
