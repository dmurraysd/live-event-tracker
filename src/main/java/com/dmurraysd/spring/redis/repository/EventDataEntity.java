package com.dmurraysd.spring.redis.repository;

import com.dmurraysd.spring.model.EventStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "event-status-cache")
public record EventDataEntity(@Id String eventId, @Indexed EventStatus eventStatus) {

}
