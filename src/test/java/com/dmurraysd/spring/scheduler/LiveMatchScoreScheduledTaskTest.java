package com.dmurraysd.spring.scheduler;

import com.dmurraysd.spring.config.RedisTestConfig;
import com.dmurraysd.spring.kafka.ScoreUpdateKafkaProducer;
import com.dmurraysd.spring.kafka.ScoreUpdateKafkaProducerConfig;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("scheduler")
@SpringBootTest(properties = {"scheduled.job.fixed.delay=2000"})
class LiveMatchScoreScheduledTaskTest {

    @MockitoBean
    private ScoreUpdateKafkaProducerConfig scoreUpdateKafkaProducerConfig;

    @MockitoBean
    private ScoreUpdateKafkaProducer scoreUpdateKafkaProducer;

    @MockitoSpyBean
    private LiveMatchScoreScheduledTask liveMatchScoreScheduledTask;

    @MockitoBean
    private LiveEventTrackerService liveEventTrackerService;

    @Test
    void publishLiveMatchScores() {
        doNothing().when(liveEventTrackerService).publishLiveMatchScores(any());

        await().atMost(Duration.ofSeconds(5L))
                        .untilAsserted(() -> {
                            verify(liveMatchScoreScheduledTask, atLeast(2)).publishLiveMatchScores();
                        });
    }
}