package com.dmurraysd.spring.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventDataRequest(@NotBlank(message = "Invalid eventId: Event Id is empty") String eventId,
                               @NotNull(message = "Invalid status: Status is null") EventStatus status) {
}
