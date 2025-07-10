package com.dmurraysd.spring;

import com.dmurraysd.spring.config.RedisTestConfig;
import com.dmurraysd.spring.config.kafka.KafkaTestConsumer;
import com.dmurraysd.spring.model.EventDataRequest;
import com.dmurraysd.spring.model.MatchScore;
import com.dmurraysd.spring.utils.TestUtils;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static com.dmurraysd.spring.utils.TestUtils.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"kafka.broker.address=${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}",
                "scheduled.job.fixed.delay=PT3S"})
@EmbeddedKafka(partitions = 1,
        topics = {"${kafka.topic.outbound:scoreUpdates}"},
        brokerProperties = {"log.dir=target/kafka/${random.uuid}/"})
class LiveEventTrackerApplicationTest {

    @RegisterExtension
    private static final WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8085)).build();

    @LocalServerPort
    private Integer serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private KafkaTestConsumer kafkaTestConsumer;


    @DisplayName("Client sends live update for an event and event update is sent to topic")
    @Test
    void shouldProcessIncomingRequestAndPublishLiveEventUpdatesToTopic() {
        final List<EventDataRequest> eventDataRequests = TestUtils.readJsonResourceToList("integration/valid/events.json", EventDataRequest.class);
        final List<MatchScore> expectedMatchScores = readJsonResourceToList("integration/valid/event-scores.json", MatchScore.class);

        wireMockServer.stubFor(get(urlEqualTo(format("/scores/%s", expectedMatchScores.getFirst().eventId())))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(serialise(expectedMatchScores.getFirst())))
        );

        final String baseUrl = String.format("http://localhost:%s/", serverPort);
        final URI uri = UriComponentsBuilder.fromUri(URI.create(baseUrl)).path("/events/status").build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        eventDataRequests.stream()
                .map(eventDataRequest -> new HttpEntity<>(eventDataRequest, headers))
                .map(eventDataRequestHttpEntity -> testRestTemplate.exchange(uri, HttpMethod.POST, eventDataRequestHttpEntity, String.class))
                .forEach(response -> assertEquals(HttpStatus.OK, response.getStatusCode()));

        await().atMost(Duration.ofSeconds(4L))
                .untilAsserted(() -> {
                    assertEquals(1, kafkaTestConsumer.getAllRecords().size());
                });

        assertEquals(1, kafkaTestConsumer.getAllRecords().size());
        MatchScore actualMatchScore = deSerialize(kafkaTestConsumer.getLastMessage().value(), MatchScore.class);
        MatchScore expectedMatchScore = expectedMatchScores.getFirst();

        assertEquals(expectedMatchScore, actualMatchScore);
        wireMockServer.verify(getRequestedFor(urlEqualTo(format("/scores/%s", expectedMatchScores.getFirst().eventId()))));
    }

    @Test
    void shouldProcessHandleRequestWithInvalidEventId() {
        final String eventDataRequest = readJsonResourceToString("integration/invalid/invalid-event-id.json");

        final String baseUrl = String.format("http://localhost:%s/", serverPort);
        final URI uri = UriComponentsBuilder.fromUri(URI.create(baseUrl)).path("/events/status").build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(eventDataRequest, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        await().pollDelay(Duration.ofSeconds(1L))
                .atMost(Duration.ofSeconds(4L))
                .untilAsserted(() -> {
                    assertEquals(0, kafkaTestConsumer.getAllRecords().size());
                });

        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/scores/%s")));
    }

    @Test
    void shouldProcessHandleRequestWithInvalidEventStatus() {
        final String eventDataRequest = readJsonResourceToString("integration/invalid/invalid-event-status.json");

        final String baseUrl = String.format("http://localhost:%s/", serverPort);
        final URI uri = UriComponentsBuilder.fromUri(URI.create(baseUrl)).path("/events/status").build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(eventDataRequest, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        await().pollDelay(Duration.ofSeconds(1L))
                .atMost(Duration.ofSeconds(4L))
                .untilAsserted(() -> {
                    assertEquals(0, kafkaTestConsumer.getAllRecords().size());
                });

        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/scores/%s")));
    }
}
