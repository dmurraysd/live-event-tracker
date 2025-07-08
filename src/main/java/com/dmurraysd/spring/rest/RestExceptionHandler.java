package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.rest.exception.LiveEventTrackerError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<LiveEventTrackerError> handleException(Throwable ex) {
        final String details = Objects.nonNull(ex) ? ex.getClass().getSimpleName() : "Unknown";

        final LiveEventTrackerError liveEventTrackerError =
                new LiveEventTrackerError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), details);

        return new ResponseEntity<>(liveEventTrackerError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
