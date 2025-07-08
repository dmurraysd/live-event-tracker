package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class EventStatusControllerTest {

    private final LiveEventTrackerService liveEventTrackerService = mock(LiveEventTrackerService.class);
    private final EventStatusController eventStatusController = new EventStatusController(liveEventTrackerService);


    @Test
    void shouldProcessEventStatusMessageAndReturnEventStatus() {
        String eventId = "eventId";
        EventStatus status = EventStatus.LIVE;
        EventDataRequest message = new EventDataRequest(eventId, status);

        ResponseEntity<EventDataRequest> actualResponse = eventStatusController.updateEventStatus(message);

        ResponseEntity<EventDataRequest> expectedResponse = ResponseEntity.ok(message);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    }
}
