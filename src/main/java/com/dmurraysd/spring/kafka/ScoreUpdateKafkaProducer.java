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

import static com.dmurraysd.spring.logging.LoggingUtil.formatLogMessage;

@Configuration
public class ScoreUpdateKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ScoreUpdateKafkaProducer.class);

    private static final String CORRELATION_ID = "spyCorrelationId";
    private static final String SOURCE_ID = "spySourceId";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String kafkaTopic;

    public ScoreUpdateKafkaProducer(@Value("${kafka.topic.outbound}") final String kafkaTopic,
                                    final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
    }

    public CompletableFuture<Void> send(MatchScore matchScore, IdProvider context) {
        Message<Object> messageToProduce = MessageBuilder.withPayload(Objects.requireNonNullElse(matchScore, KafkaNull.INSTANCE))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                .setHeader(KafkaHeaders.KEY, matchScore.eventId())
                .setHeader(CORRELATION_ID, context.getCorrelationId())
                .setHeader(SOURCE_ID, context.getSourceId())
                .build();

        return kafkaTemplate.send(messageToProduce)
                .handle((result, exception) -> {
                    if (exception == null) {
                        logger.info(formatLogMessage(context, "Published message to downstream topic %s", kafkaTopic));
                    } else {
                        logger.error(formatLogMessage(context, "Error when attempting to produce message with key %s to downstream topic", matchScore.eventId()), exception);
                    }
                    return null;
                });

    }
}
