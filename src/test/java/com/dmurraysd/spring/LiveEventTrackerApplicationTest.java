package com.dmurraysd.spring;

import com.dmurraysd.spring.config.RedisTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LiveEventTrackerApplicationTest {

    @DisplayName("Consumer sends live update for an event and event update is sent to topic")
    @Test
    void shouldProcessIncomingRequestAndPublishEventUpdateToTopic() {

    }

}
