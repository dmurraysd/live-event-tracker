package com.dmurraysd.spring;

import com.dmurraysd.spring.config.RedisTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = {"scheduled.job.fixed.delay=PT10S"})
@EmbeddedKafka(partitions = 10,
        topics = {"${kafka.topic.outbound:scoreUpdates}"},
        brokerProperties = {"log.dir=target/kafka/${random.uuid}/"})
class LiveEventTrackerApplicationTest {

    @DisplayName("Consumer sends live update for an event and event update is sent to topic")
    @Test
    void shouldProcessIncomingRequestAndPublishEventUpdateToTopic() {

    }

}
