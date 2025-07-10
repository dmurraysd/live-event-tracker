package com.dmurraysd.spring.service;

import com.dmurraysd.spring.client.InternalMatchScoreClient;
import com.dmurraysd.spring.kafka.ScoreUpdateKafkaProducer;
import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.model.EventData;
import com.dmurraysd.spring.model.EventStatus;
import com.dmurraysd.spring.redis.repository.EventDataEntity;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.dmurraysd.spring.logging.LoggingUtil.formatLogMessage;

@EnableRetry
@Component
public class LiveEventTrackerService {
    private static final Logger logger = LoggerFactory.getLogger(LiveEventTrackerService.class);

    private final EventDataRepository eventDataRepository;
    private final InternalMatchScoreClient internalMatchScoreClient;
    private final ScoreUpdateKafkaProducer kafkaUpdateProducer;

    public LiveEventTrackerService(final EventDataRepository eventDataRepository,
                                   final InternalMatchScoreClient internalMatchScoreClient,
                                   final ScoreUpdateKafkaProducer kafkaUpdateProducer) {
        this.eventDataRepository = eventDataRepository;
        this.internalMatchScoreClient = internalMatchScoreClient;
        this.kafkaUpdateProducer = kafkaUpdateProducer;
    }

    public EventData updateEventStatus(EventData eventData) {
        logger.info(formatLogMessage(eventData.context(), "Updating event status for event with id %s", eventData.getEventId()));
        EventDataEntity eventDataEntity = eventDataRepository.save(eventData.toEventDataEntity());
        return EventData.convertToInternal(eventDataEntity, eventData.context());
    }

    public void publishLiveMatchScores(IdProvider context) {
        logger.info(formatLogMessage(context, "Publishing of match score updates"));

        eventDataRepository.findAllByEventStatus(EventStatus.LIVE)
                .stream()
                .map(EventDataEntity::eventId)
                .map(internalMatchScoreClient::retrieveMatchScore)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(matchScore -> kafkaUpdateProducer.send(matchScore, context))
                .reduce(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null))
                .whenComplete((v, e) -> logger.info(formatLogMessage(context, "Match scores publishing complete")));

    }
}
