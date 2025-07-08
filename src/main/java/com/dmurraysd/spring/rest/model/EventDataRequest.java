package com.dmurraysd.spring.rest.model;

import com.dmurraysd.spring.rest.exception.EventIdValidationException;

import java.util.Objects;

public record EventDataRequest(String eventId, EventStatus status) {

    public EventDataRequest {
        if(Objects.isNull(eventId)) {
            throw new EventIdValidationException("eventId cannot be null");
        }
    }
}
