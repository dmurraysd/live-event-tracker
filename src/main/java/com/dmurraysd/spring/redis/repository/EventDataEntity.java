package com.dmurraysd.spring.redis.repository;

import com.dmurraysd.spring.rest.EventDataRequest;
import com.dmurraysd.spring.rest.EventStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "event-status-cache")
public record EventDataEntity(@Id String eventId, @Indexed EventStatus eventStatus) {

    public EventDataRequest toEventDataRequest() {
        return new EventDataRequest(eventId, eventStatus);
    }
}
