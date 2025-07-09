package com.dmurraysd.spring.logging;

import org.springframework.data.annotation.Id;

import java.util.Optional;

public record IdContextProvider(String eventId, String correlationId, String sourceId) implements IdProvider {

    @Override
    public String getEventId() {
        return Optional.ofNullable(this.eventId).orElse("-");
    }

    @Override
    public String getCorrelationId() {
        return Optional.ofNullable(this.correlationId).orElse("-");
    }

    @Override
    public String getSourceId() {
        return Optional.ofNullable(this.sourceId).orElse("-");
    }
}
