package com.dmurraysd.spring.service;

import com.dmurraysd.spring.client.InternalMatchScoreClient;
import com.dmurraysd.spring.kafka.KafkaUpdateProducer;
import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.redis.repository.EventDataEntity;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import com.dmurraysd.spring.rest.model.EventData;
import com.dmurraysd.spring.rest.model.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class LiveEventTrackerService {
    private static final Logger logger = LoggerFactory.getLogger(LiveEventTrackerService.class);

    private final EventDataRepository eventDataRepository;
    private final InternalMatchScoreClient internalMatchScoreClient;
    private final KafkaUpdateProducer kafkaUpdateProducer;

    public LiveEventTrackerService(final EventDataRepository eventDataRepository,
                                   final InternalMatchScoreClient internalMatchScoreClient,
                                   final KafkaUpdateProducer kafkaUpdateProducer) {
        this.eventDataRepository = eventDataRepository;
        this.internalMatchScoreClient = internalMatchScoreClient;
        this.kafkaUpdateProducer = kafkaUpdateProducer;
    }

    public EventData updateEventStatus(EventData eventData) {
        EventDataEntity eventDataEntity = eventDataRepository.save(eventData.toEventDataEntity());
        return EventData.convertToInternal(eventDataEntity, eventData.context());
    }

    public void publishLiveMatchScores(IdProvider context) {
        eventDataRepository.findAllByEventStatus(EventStatus.LIVE)
                .stream()
                .map(EventDataEntity::eventId)
                .map(internalMatchScoreClient::retrieveMatchScore)
                .filter(matchScoreResponseEntity -> matchScoreResponseEntity.getStatusCode().is2xxSuccessful())
                .map(HttpEntity::getBody)
                .map(matchScore -> kafkaUpdateProducer.send(matchScore, context))
                .reduce(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null))
                .whenComplete((v, e)-> System.out.println("Match scores published"));

    }
}
