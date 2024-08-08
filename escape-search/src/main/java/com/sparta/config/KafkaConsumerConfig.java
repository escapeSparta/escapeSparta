package com.sparta.config;

import com.sparta.domain.review.dto.KafkaReviewRequestDto;
import com.sparta.domain.review.dto.KafkaReviewResponseDto;
import com.sparta.domain.theme.dto.KafkaThemeInfoRequestDto;
import com.sparta.domain.theme.dto.KafkaThemeInfoResponseDto;
import com.sparta.domain.theme.dto.KafkaThemeTimeRequestDto;
import com.sparta.domain.theme.dto.KafkaThemeTimeResponseDto;
import com.sparta.global.exception.customException.GlobalCustomException;
import com.sparta.kafkaError.KafkaErrorHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaErrorHandler kafkaErrorHandler;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> KafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(String.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaReviewRequestDto> reviewRequestKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaReviewRequestDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaReviewResponseDto> reviewResponseKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaReviewResponseDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaThemeInfoRequestDto> themeInfoRequestKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaThemeInfoRequestDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaThemeInfoResponseDto> themeInfoResponseKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaThemeInfoResponseDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaThemeTimeRequestDto> themeTimeRequestKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaThemeTimeRequestDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaThemeTimeResponseDto> themeTimeResponseKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(KafkaThemeTimeResponseDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GlobalCustomException> exceptionKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(GlobalCustomException.class);
    }


    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        factory.setCommonErrorHandler(kafkaErrorHandler.customErrorHandler());
        return factory;
    }

    @Bean
    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.sparta.domain.*");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}