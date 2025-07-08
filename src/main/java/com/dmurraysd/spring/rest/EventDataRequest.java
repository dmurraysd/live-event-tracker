package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.redis.repository.EventDataEntity;
import com.dmurraysd.spring.rest.exception.EventIdValidationException;

import java.util.Objects;

public record EventDataRequest(String eventId, EventStatus status) {

    public EventDataRequest {
        if(Objects.isNull(eventId)) {
            throw new EventIdValidationException("eventId cannot be null");
        }
    }

    public EventDataEntity toEventDataEntity() {
        return new EventDataEntity(eventId, status);
    }
}
