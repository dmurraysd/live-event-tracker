package com.dmurraysd.spring.client;

import com.dmurraysd.spring.model.MatchScore;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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

    Logger logger = LoggerFactory.getLogger(InternalMatchScoreClient.class);

    @Valid
    @Retryable(retryFor = ServerException.class, maxAttemptsExpression = "${internal.match.score.client.retry:3}",
        backoff = @Backoff(delayExpression = "${internal.match.score.client.backoff:50}")
    )
    @GetExchange("/scores/{eventId}")
    Optional<MatchScore> retrieveMatchScore(@PathVariable String eventId);

    @Recover
    default Optional<MatchScore> recover(ServerException ex) {
        logger.error("Internal API call failure occurred during match score retrieval {}", ex.getMessage());
        return Optional.empty();
    }

    @Recover
    default Optional<MatchScore> recoverFromRestClientException(Exception ex) {
        logger.error("Internal API call failure occurred during match score retrieval {}", ex.getMessage());
        return Optional.empty();
    }
}
