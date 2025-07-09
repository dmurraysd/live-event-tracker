package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.logging.LoggingUtil;
import com.dmurraysd.spring.rest.model.EventData;
import com.dmurraysd.spring.rest.model.EventDataRequest;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Supplier;

@RestController
@RequestMapping(path = "/events")
public class EventStatusController {

    private static final String SOURCE_ID = "EVENT_STATUS-REST_API";

    private final LiveEventTrackerService liveEventTrackerService;
    private final Supplier<UUID> uuidSupplier;

    public EventStatusController(final LiveEventTrackerService liveEventTrackerService,
                                 final Supplier<UUID> uuidSupplier) {
        this.liveEventTrackerService = liveEventTrackerService;
        this.uuidSupplier = uuidSupplier;
    }

    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDataRequest> updateEventStatus(@RequestBody EventDataRequest eventDataRequest) {
        IdProvider context = LoggingUtil.loggingContext(eventDataRequest, uuidSupplier.get(), SOURCE_ID);

        EventData eventData = liveEventTrackerService.updateEventStatus(EventData.convertToInternal(eventDataRequest, context));

        return ResponseEntity.ok(eventData.toEventDataRequest());
    }
}
