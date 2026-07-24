package com.__buy.user_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.__buy.user_service.dto.ImageDeletedEvent;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;

    @Bean
    ConsumerFactory<String, ImageDeletedEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaServers);

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "user-service");

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");

        JacksonJsonDeserializer<ImageDeletedEvent> deserializer = new JacksonJsonDeserializer<>(
                ImageDeletedEvent.class);

        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ImageDeletedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, ImageDeletedEvent> consumerFactory) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, ImageDeletedEvent>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}