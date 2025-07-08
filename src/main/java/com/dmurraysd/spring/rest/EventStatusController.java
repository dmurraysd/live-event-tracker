package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CacheConfig(cacheNames = "event-status-cache")
@RestController
@RequestMapping(path = "/events")
public class EventStatusController {

    private final LiveEventTrackerService liveEventTrackerService;

    public EventStatusController(final LiveEventTrackerService liveEventTrackerService) {
        this.liveEventTrackerService = liveEventTrackerService;
    }

    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDataRequest> updateEventStatus(@RequestBody EventDataRequest eventDataRequest) {
        liveEventTrackerService.updateEventStatus(eventDataRequest);

        return ResponseEntity.ok(eventDataRequest);
    }
}
