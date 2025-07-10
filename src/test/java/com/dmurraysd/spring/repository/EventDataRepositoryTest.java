package com.dmurraysd.spring.repository;

import com.dmurraysd.spring.config.RedisTestConfig;
import com.dmurraysd.spring.model.EventStatus;
import com.dmurraysd.spring.redis.repository.EventDataEntity;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {RedisTestConfig.class})
@DataRedisTest(properties = {"spring.data.redis.port=6390",
        "spring.data.redis.host=localhost"})
class EventDataRepositoryTest {

    @Autowired
    private EventDataRepository eventDataRepository;

    @Test
    void findAllByEventStatus() {
        EventDataEntity eventData = new EventDataEntity("ss", EventStatus.LIVE);
        eventDataRepository.save(eventData);

        EventDataEntity eventData2 = new EventDataEntity("ss2", EventStatus.NOT_LIVE);
        eventDataRepository.save(eventData2);

        List<EventDataEntity> events = eventDataRepository.findAllByEventStatus(EventStatus.LIVE);
        assertEquals(1, events.size());
        EventDataEntity found = events.get(0);
        assertEquals(eventData, found);
    }
}