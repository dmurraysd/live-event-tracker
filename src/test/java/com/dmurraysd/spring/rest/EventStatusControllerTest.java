package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.logging.IdContextProvider;
import com.dmurraysd.spring.rest.model.EventData;
import com.dmurraysd.spring.rest.model.EventDataRequest;
import com.dmurraysd.spring.rest.model.EventStatus;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventStatusControllerTest {

    private final LiveEventTrackerService liveEventTrackerService = mock(LiveEventTrackerService.class);
    private final EventStatusController eventStatusController = new EventStatusController(liveEventTrackerService, UUID::randomUUID);


    @Test
    void shouldProcessEventStatusMessageAndReturnEventStatus() {
        String eventId = "eventId";
        EventStatus status = EventStatus.LIVE;
        EventDataRequest eventDataRequest = new EventDataRequest(eventId, status);
        EventData eventData = new EventData(eventId, status, new IdContextProvider(eventId, "CID", "SID"));
        when(liveEventTrackerService.updateEventStatus(EventData.convertToInternal(eventDataRequest, any()))).thenReturn(eventData);

        ResponseEntity<EventDataRequest> actualResponse = eventStatusController.updateEventStatus(eventDataRequest);

        ResponseEntity<EventDataRequest> expectedResponse = ResponseEntity.ok(eventDataRequest);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertEquals(expectedResponse.getBody(), actualResponse.getBody());
        verify(liveEventTrackerService).updateEventStatus(EventData.convertToInternal(eventDataRequest, any()));
    }
}
