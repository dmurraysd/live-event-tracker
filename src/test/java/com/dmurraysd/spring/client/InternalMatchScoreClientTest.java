package com.dmurraysd.spring.client;

import com.dmurraysd.spring.model.MatchScore;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.dmurraysd.spring.utils.TestUtils.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

class InternalMatchScoreClientTest {

    @RegisterExtension
    private static final WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8081)).build();

    private static final String TEST_API_URL = "http://localhost:" + "8081";//wireMockServer.getPort();

    private final RestClientConfig restClientConfig = new RestClientConfig();
    private InternalMatchScoreClient internalMatchScoreClient;

    @BeforeEach
    void setUp() {
        internalMatchScoreClient = restClientConfig.internalMatchScoreClient(TEST_API_URL + "/", RestClient.builder().baseUrl(TEST_API_URL));
    }

    @Test
    void shouldRetrieveMatchScore() {
        final MatchScore matchScore = readJsonResourceToObject("client/match-score.json", MatchScore.class);

        wireMockServer.stubFor(get(urlEqualTo(format("/scores/%s", matchScore.eventId())))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(serialise(matchScore)))
        );

        final MatchScore actualMatchScore = internalMatchScoreClient.retrieveMatchScore(matchScore.eventId()).get();

        assertEquals(matchScore, actualMatchScore);
        wireMockServer.verify(getRequestedFor(urlEqualTo(format("/scores/%s", matchScore.eventId()))));
    }

    @Test
    void shouldThrowServerExceptionWhenInternalServerError() {
        final MatchScore matchScore = readJsonResourceToObject("client/match-score.json", MatchScore.class);

        wireMockServer.stubFor(
                get(urlEqualTo(format("/scores/%s", matchScore.eventId())))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(serialise(matchScore)))
        );

        final String actualMessage = assertThrows(ServerException.class,
                () -> internalMatchScoreClient.retrieveMatchScore(matchScore.eventId())).getMessage();

        final String expectedMessage = format("Internal Server Error Status 500 [http://localhost:8081/scores/%s]", matchScore.eventId());

        assertEquals(expectedMessage, actualMessage);
        wireMockServer.verify(1, getRequestedFor(urlEqualTo(format("/scores/%s", matchScore.eventId()))));
    }

    @Test
    void shouldProcess4xxError() {
        final MatchScore matchScore = readJsonResourceToObject("client/match-score.json", MatchScore.class);

        wireMockServer.stubFor(
                get(urlEqualTo(format("/scores/%s", matchScore.eventId())))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        final Optional<MatchScore> actualMatchScore = internalMatchScoreClient.retrieveMatchScore(matchScore.eventId());

        assertTrue(actualMatchScore.isEmpty());
        wireMockServer.verify(1, getRequestedFor(urlEqualTo(format("/scores/%s", matchScore.eventId()))));
    }
}