package com.dmurraysd.spring.kafka;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Configuration
public class KafkaUpdateProducer {

    private static final Logger logger = LoggerFactory.getLogger(LiveEventTrackerService.class);
    private static final String CORRELATION_ID = "spyCorrelationId";
    private static final String SOURCE_ID = "spySourceId";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String kafkaTopic;

    public KafkaUpdateProducer(final KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${kafka.outbound.topic}") String kafkaTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
    }

    public CompletableFuture<Void> send(MatchScore message, IdProvider context) {
        Message<Object> messageToProduce = MessageBuilder.withPayload(Objects.requireNonNullElse(message, KafkaNull.INSTANCE))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                .setHeader(KafkaHeaders.KEY, message.eventId())
                .setHeader(CORRELATION_ID, context.getCorrelationId())
                .setHeader(SOURCE_ID, context.getSourceId())
                .build();

        return kafkaTemplate.send(messageToProduce)
                .handle((result, exception) -> {
                    if (exception == null) {
                        return null;
                    } else {
                        return null;
                    }
                });

    }
}
