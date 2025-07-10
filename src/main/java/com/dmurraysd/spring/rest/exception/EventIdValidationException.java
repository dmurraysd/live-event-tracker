package com.dmurraysd.spring.rest.exception;

public class EventIdValidationException extends RuntimeException {
    public EventIdValidationException(String eventIdCannotBeNull) {
        super(eventIdCannotBeNull);
    }
}
