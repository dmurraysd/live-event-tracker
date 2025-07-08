package com.dmurraysd.spring.rest.exception;

public record LiveEventTrackerError(int code, String message, String details) {

    public static LiveEventTrackerError of(int code, String message, String details) {
        return new LiveEventTrackerError(code, message, details);
    }
}
