package com.dmurraysd.spring.logging;

import com.dmurraysd.spring.rest.model.EventDataRequest;
import org.apache.logging.log4j.core.LogEvent;

import java.util.Arrays;
import java.util.UUID;

import static java.lang.String.format;

public class LoggingUtil {

    private LoggingUtil() {
    }

    public static IdProvider loggingContext(EventDataRequest eventDataRequest, UUID uuid, String sourceId) {
        return new IdContextProvider(eventDataRequest.eventId(), uuid.toString(), sourceId);
    }

    public static IdContextProvider toJobLoggingContext(UUID uuid, String sourceId) {
        return new IdContextProvider(null, uuid.toString(), sourceId);
    }

    public static String formatLogMessage(IdProvider loggingContext, String message, Object... args) {
        return String.format("eventId [%s] correlationId [%s] sourceId [%s] - %s",
                loggingContext.getEventId(), loggingContext.getCorrelationId(), loggingContext.getSourceId(), format(message, args));
    }
}
