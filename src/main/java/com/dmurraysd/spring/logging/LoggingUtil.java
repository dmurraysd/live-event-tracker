package com.dmurraysd.spring.logging;

import com.dmurraysd.spring.rest.model.EventDataRequest;

import java.util.UUID;

public class LoggingUtil {

    private LoggingUtil() {
    }

    public static IdProvider loggingContext(EventDataRequest eventDataRequest, UUID uuid, String sourceId) {
        return new IdContextProvider(eventDataRequest.eventId(), uuid.toString(), sourceId);
    }

    public static IdContextProvider toJobLoggingContext(UUID uuid, String sourceId) {
        return new IdContextProvider(null, uuid.toString(), sourceId);
    }
}
