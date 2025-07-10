package com.dmurraysd.spring.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import wiremock.com.google.common.collect.ImmutableMap;

@TestConfiguration
@EnableRetry
@Profile("test")
public class KafkaTestConfig {

    private final String brokerAddress;

    public KafkaTestConfig(@Value("${kafka.broker.address}") final String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    @Bean
    @Qualifier("testConsumerFactory")
    public ConsumerFactory<String, String> testConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "test_group")
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true)
                .put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000)
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .build(), new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> testKafkaListenerContainerFactory(
            @Qualifier("testConsumerFactory") final ConsumerFactory<String, String> testConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.setConsumerFactory(testConsumerFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> testProducerFactory() {
        return new DefaultKafkaProducerFactory<>(ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1)
                .build());
    }

    @Bean
    public KafkaTemplate<String, Object> testKafkaTemplate(ProducerFactory<String, Object> testProducerFactory) {
        return new KafkaTemplate<>(testProducerFactory);
    }
}
