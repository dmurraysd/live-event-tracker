package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.logging.LoggingUtil;
import com.dmurraysd.spring.model.EventData;
import com.dmurraysd.spring.model.EventDataRequest;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.function.Supplier;

import static com.dmurraysd.spring.logging.LoggingUtil.formatLogMessage;

@RestController
@RequestMapping(path = "/events")
public class EventStatusController {

    private static final Logger logger = LoggerFactory.getLogger(EventStatusController.class);
    private static final String SOURCE_ID = "event-status-rest-api";

    private final LiveEventTrackerService liveEventTrackerService;
    private final Supplier<UUID> uuidSupplier;

    public EventStatusController(final LiveEventTrackerService liveEventTrackerService,
                                 final Supplier<UUID> uuidSupplier) {
        this.liveEventTrackerService = liveEventTrackerService;
        this.uuidSupplier = uuidSupplier;
    }

    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE) //produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDataRequest> updateEventStatus(@Valid @RequestBody EventDataRequest eventDataRequest) {
        IdProvider context = LoggingUtil.loggingContext(eventDataRequest, uuidSupplier.get(), SOURCE_ID);

        logger.info(formatLogMessage(context, "Updating status of event %s", eventDataRequest));
        EventData eventData = liveEventTrackerService.updateEventStatus(EventData.convertToInternal(eventDataRequest, context));

        return ResponseEntity.ok(eventData.toEventDataRequest());
    }
}
