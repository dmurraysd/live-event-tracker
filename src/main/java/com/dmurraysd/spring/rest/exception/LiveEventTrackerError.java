package com.dmurraysd.spring.rest.exception;

public record LiveEventTrackerError<T>(int code, String message, T details) {

    public LiveEventTrackerError<T> of(int code, String message, T details) {
        return new LiveEventTrackerError<>(code, message, details);
    }
}
