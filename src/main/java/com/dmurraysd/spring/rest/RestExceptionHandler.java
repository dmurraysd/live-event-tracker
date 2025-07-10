package com.dmurraysd.spring.rest;

import com.dmurraysd.spring.client.ServerException;
import com.dmurraysd.spring.rest.exception.LiveEventTrackerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public LiveEventTrackerError<String> handleException(Throwable ex) {
        final String details = Objects.nonNull(ex) ? ex.getClass().getSimpleName() : "Unknown";

        final LiveEventTrackerError<String> liveEventTrackerError =
                new LiveEventTrackerError<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), details);
        logger.error(liveEventTrackerError.toString());
        return liveEventTrackerError;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LiveEventTrackerError<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = Optional.of(ex.getBindingResult())
                .map(Errors::getFieldErrors)
                .stream()
                .flatMap(Collection::stream)
                .map(FieldError::getDefaultMessage)
                .toList();

        final Map<String, List<String>> details = Map.of("errors", errors);

        final LiveEventTrackerError<Map<String, List<String>>> liveEventTrackerError =
                new LiveEventTrackerError<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), details);
        logger.error(liveEventTrackerError.toString());
        return liveEventTrackerError;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public LiveEventTrackerError<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        final String details = ex.getMessage();

        final LiveEventTrackerError<String> liveEventTrackerError =
                new LiveEventTrackerError<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), details);

        logger.error(liveEventTrackerError.toString());

        return liveEventTrackerError;
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public LiveEventTrackerError<String> handleServerException(ServerException ex) {
        final LiveEventTrackerError<String> liveEventTrackerError =
                new LiveEventTrackerError<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        ex.getMessage());

        logger.error(liveEventTrackerError.toString());

        return liveEventTrackerError;
    }

}
