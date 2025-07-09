package com.dmurraysd.spring.client;

import com.dmurraysd.spring.kafka.MatchScore;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface InternalMatchScoreClient {

    @Retryable(retryFor = ServerException.class, maxAttemptsExpression = "${internal.match.score.client.retry:3}",
    backoff = @Backoff(delayExpression = "${internal.match.score.client.backoff:50}"))
    @GetExchange("/scores/{eventId}")
    Optional<MatchScore> retrieveMatchScore(@PathVariable String eventId);

    @Recover
    default Optional<MatchScore> recover(ServerException ex) {
        System.out.println("hello2");
        return Optional.empty();
    }

    @Recover
    default Optional<MatchScore> recoverFromRestClientException(RestClientException e) {
        System.out.println("hellozz");
        return Optional.empty();
    }
}
