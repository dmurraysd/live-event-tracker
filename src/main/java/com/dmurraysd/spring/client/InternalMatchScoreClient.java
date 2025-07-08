package com.dmurraysd.spring.client;

import com.dmurraysd.spring.kafka.MatchScore;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface InternalMatchScoreClient {

    @Retryable(retryFor = ServerException.class, maxAttemptsExpression = "${internal.match.score.client.retry:3}",
    backoff = @Backoff(delayExpression = "${internal.match.score.client.backoff:50}"))
    @GetExchange("/{eventId}")
    ResponseEntity<MatchScore> retrieveMatchScore(@PathVariable String eventId);

    @Recover
    default ResponseEntity<MatchScore> recover(ServerException ex) {
        return null;
    }
}
