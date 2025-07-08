package com.dmurraysd.spring.service;

import com.dmurraysd.spring.redis.repository.EventDataRepository;
import com.dmurraysd.spring.rest.EventDataRequest;
import com.dmurraysd.spring.rest.EventStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveEventTrackerServiceTest {

    private final EventDataRepository eventDataRepository = mock(EventDataRepository.class);
    private final LiveEventTrackerService liveEventTrackerService = new LiveEventTrackerService(eventDataRepository);

    @Test
    void shouldUpdateEventStatus() {
        EventDataRequest eventDataRequest = new EventDataRequest("eventId", EventStatus.LIVE);
        when(eventDataRepository.save(eventDataRequest.toEventDataEntity())).thenReturn(eventDataRequest.toEventDataEntity());

        EventDataRequest actualEventDataRequest = liveEventTrackerService.updateEventStatus(eventDataRequest);

        assertEquals(eventDataRequest, actualEventDataRequest);
        verify(eventDataRepository).save(eventDataRequest.toEventDataEntity());
    }
}