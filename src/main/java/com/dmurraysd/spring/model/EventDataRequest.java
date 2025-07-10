package com.dmurraysd.spring.model;

import com.dmurraysd.spring.rest.exception.EventIdValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record EventDataRequest(@NotBlank(message = "Invalid eventId: Event Id is empty") String eventId,
                               @NotNull(message = "Invalid status: Status is null") EventStatus status) {

    public EventDataRequest {
        if(Objects.isNull(eventId)) {
            throw new EventIdValidationException("eventId cannot be null");
        }
       /* if(Objects.isNull(status)) {
            throw new EventIdValidationException("status cannot be null");
        }*/
    }

}
