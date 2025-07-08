package com.dmurraysd.spring.service;

import com.dmurraysd.spring.logging.IdContextProvider;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import com.dmurraysd.spring.rest.model.EventData;
import com.dmurraysd.spring.rest.model.EventDataRequest;
import com.dmurraysd.spring.rest.model.EventStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveEventTrackerServiceTest {

    private final EventDataRepository eventDataRepository = mock(EventDataRepository.class);
    private final LiveEventTrackerService liveEventTrackerService = new LiveEventTrackerService(eventDataRepository);

    @Test
    void shouldUpdateEventStatus() {
        EventDataRequest eventDataRequest = new EventDataRequest("eventId", EventStatus.LIVE);
        EventData eventData = EventData.convertToInternal(eventDataRequest, new IdContextProvider(eventDataRequest.eventId(), "CID", "SID"));
        when(eventDataRepository.save(eventData.toEventDataEntity())).thenReturn(eventData.toEventDataEntity());

        EventData actualEventDataRequest = liveEventTrackerService.updateEventStatus(eventData);

        assertEquals(eventData, actualEventDataRequest);
        verify(eventDataRepository).save(eventData.toEventDataEntity());
    }
}