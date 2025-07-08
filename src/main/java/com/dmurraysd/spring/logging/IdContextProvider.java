package com.dmurraysd.spring.logging;

public record IdContextProvider(String eventId, String correlationId, String sourceId) implements IdProvider {

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
