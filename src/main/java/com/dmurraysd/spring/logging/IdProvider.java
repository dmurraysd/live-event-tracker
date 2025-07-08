package com.dmurraysd.spring.logging;

public interface IdProvider {
    String getEventId();
    String getCorrelationId();
    String getSourceId();
}
