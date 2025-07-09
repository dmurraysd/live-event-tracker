package com.dmurraysd.spring.logging;

import org.springframework.data.annotation.Id;

public record IdContextProvider(String eventId, String correlationId, String sourceId) implements IdProvider {

    public static IdContextProvider toJobIdContextProvider(String correlationId, String sourceId) {
        return new IdContextProvider(null, correlationId, sourceId);
    }

    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String getCorrelationId() {
        return this.correlationId;
    }

    @Override
    public String getSourceId() {
        return this.sourceId;
    }
}
