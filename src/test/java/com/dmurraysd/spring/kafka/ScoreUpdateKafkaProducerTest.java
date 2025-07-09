package com.dmurraysd.spring.kafka;

import com.dmurraysd.spring.logging.IdContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScoreUpdateKafkaProducerTest {

    public static final String TEST_TOPIC = "testTopic";

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final ScoreUpdateKafkaProducer scoreUpdateKafkaProducer = new ScoreUpdateKafkaProducer(TEST_TOPIC, kafkaTemplate);
    private final MatchScore matchScore = new MatchScore("eventId", "0:0");
    private ArgumentCaptor<Message<MatchScore>> messageArgumentCaptor;

    @BeforeEach
    void setUp() {
        messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    }

    @Test
    void shouldSendMessage() {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(new CompletableFuture<>());

        final MatchScore matchScore = new MatchScore("eventId", "0:0");
        scoreUpdateKafkaProducer.send(matchScore, new IdContextProvider("eventId", "cid", "sid"));

        verify(kafkaTemplate).send(messageArgumentCaptor.capture());
        assertEquals(messageArgumentCaptor.getValue().getHeaders().get(KafkaHeaders.KEY), matchScore.eventId());
        assertEquals(matchScore, messageArgumentCaptor.getValue().getPayload());
    }

    @Test
    void shouldHandleExceptionOnKafkaTemplateSend() throws ExecutionException, InterruptedException {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        CompletableFuture<Void> returnedCompletableFuture = scoreUpdateKafkaProducer.send(matchScore, new IdContextProvider("eventId", "cid", "sid"));

        verify(kafkaTemplate).send(any(Message.class));
        assertEquals(CompletableFuture.completedFuture(null).get(), returnedCompletableFuture.get());
    }

    @Test
    void shouldHandleResultOnKafkaTemplateSend() throws ExecutionException, InterruptedException {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));

        CompletableFuture<Void> returnedCompletableFuture = scoreUpdateKafkaProducer.send(matchScore, new IdContextProvider("eventId", "cid", "sid"));

        verify(kafkaTemplate).send(any(Message.class));
        assertEquals(CompletableFuture.completedFuture(null).get(), returnedCompletableFuture.get());
    }
}