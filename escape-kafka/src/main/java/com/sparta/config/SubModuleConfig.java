package com.sparta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AppConfig.class) // 최상위 모듈의 설정을 import
public class SubModuleConfig {
    // 설정 클래스
}