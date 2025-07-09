package com.dmurraysd.spring.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestProducer {

    private final KafkaTemplate<String, Object> testKafkaTemplate;

    public KafkaTestProducer(final KafkaTemplate<String, Object> testKafkaTemplate) {
        this.testKafkaTemplate = testKafkaTemplate;
    }

    public void send(final Message<Object> msg, final String topic) {
        System.out.printf("[TEST] sending test message to topic %s", topic);
        testKafkaTemplate.send(msg);
    }
}
