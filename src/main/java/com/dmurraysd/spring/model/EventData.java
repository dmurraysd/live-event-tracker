package com.dmurraysd.spring.model;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.redis.repository.EventDataEntity;

public record EventData(String eventId, EventStatus eventStatus, IdProvider context) implements IdProvider {

    public static EventData convertToInternal(EventDataRequest eventDataRequest, IdProvider context) {
        return new EventData(eventDataRequest.eventId(), eventDataRequest.status(), context);
    }

    public static EventData convertToInternal(EventDataEntity eventDataEntity, IdProvider context) {
        return new EventData(eventDataEntity.eventId(),eventDataEntity.eventStatus(),context);
    }

    public EventDataRequest toEventDataRequest() {
        return new EventDataRequest(this.eventId, this.eventStatus);
    }

    public EventDataEntity toEventDataEntity() {
        return new EventDataEntity(eventId, eventStatus);
    }


    @Override
    public String getEventId() {
        return context.getEventId();
    }

    @Override
    public String getCorrelationId() {
        return context.getCorrelationId();
    }

    @Override
    public String getSourceId() {
        return context.getSourceId();
    }

}
