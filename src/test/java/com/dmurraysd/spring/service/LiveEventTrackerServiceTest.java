package com.dmurraysd.spring.service;

import com.dmurraysd.spring.client.InternalMatchScoreClient;
import com.dmurraysd.spring.client.RestClientConfig;
import com.dmurraysd.spring.client.ServerException;
import com.dmurraysd.spring.config.RedisTestConfig;
import com.dmurraysd.spring.kafka.KafkaUpdateProducer;
import com.dmurraysd.spring.kafka.KafkaUpdateProducerConfig;
import com.dmurraysd.spring.kafka.MatchScore;
import com.dmurraysd.spring.logging.IdContextProvider;
import com.dmurraysd.spring.redis.repository.EventDataRepository;
import com.dmurraysd.spring.rest.model.EventData;
import com.dmurraysd.spring.rest.model.EventDataRequest;
import com.dmurraysd.spring.rest.model.EventStatus;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.dmurraysd.spring.utils.TestUtils.readJsonResourceToObject;
import static com.dmurraysd.spring.utils.TestUtils.serialise;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LiveEventTrackerService.class, RestClientConfig.class, RestClientAutoConfiguration.class,
         KafkaUpdateProducer.class, KafkaUpdateProducerConfig.class})
@TestPropertySource(properties = "spring.data.redis.port=6379")
class LiveEventTrackerServiceTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .port(8085).notifier(new ConsoleNotifier(true)))

            .build();

    @Autowired
    private LiveEventTrackerService liveEventTrackerService;

    @MockitoBean
    EventDataRepository eventDataRepository;

    @Test
    void shouldUpdateEventStatus() {
        EventDataRequest eventDataRequest = new EventDataRequest("eventId", EventStatus.LIVE);
        EventData eventData = EventData.convertToInternal(eventDataRequest, new IdContextProvider(eventDataRequest.eventId(), "CID", "SID"));
        when(eventDataRepository.save(eventData.toEventDataEntity())).thenReturn(eventData.toEventDataEntity());

        EventData actualEventDataRequest = liveEventTrackerService.updateEventStatus(eventData);

        assertEquals(eventData, actualEventDataRequest);
        verify(eventDataRepository).save(eventData.toEventDataEntity());
    }

    @Test
    void shouldRetryOnInternalServerErrorFromClient() {
        final MatchScore matchScore = readJsonResourceToObject("client/match-score.json", MatchScore.class);
        EventDataRequest eventDataRequest = new EventDataRequest(matchScore.eventId(), EventStatus.LIVE);
        EventData eventData = EventData.convertToInternal(eventDataRequest, new IdContextProvider(eventDataRequest.eventId(), "CID", "SID"));

        wireMockServer.stubFor(
                get(urlEqualTo(format("/scores/%s", matchScore.eventId())))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );
        when(eventDataRepository.findAllByEventStatus(EventStatus.LIVE)).thenReturn(List.of(eventData.toEventDataEntity()));

        liveEventTrackerService.publishLiveMatchScores(new IdContextProvider("EID", "CID", "SID"));

        verify(eventDataRepository).findAllByEventStatus(EventStatus.LIVE);
        wireMockServer.verify(3, getRequestedFor(urlEqualTo(format("/scores/%s", matchScore.eventId()))));
    }

}