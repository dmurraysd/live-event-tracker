package com.dmurraysd.spring.service;

import com.dmurraysd.spring.redis.repository.EventDataEntity;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import com.dmurraysd.spring.rest.model.EventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LiveEventTrackerService {
    private static final Logger logger = LoggerFactory.getLogger(LiveEventTrackerService.class);

    private final EventDataRepository eventDataRepository;

    public LiveEventTrackerService(EventDataRepository eventDataRepository) {
        this.eventDataRepository = eventDataRepository;
    }

    public EventData updateEventStatus(EventData eventData) {
        EventDataEntity eventDataEntity = eventDataRepository.save(eventData.toEventDataEntity());
        return EventData.convertToInternal(eventDataEntity, eventData.context());
    }

}
