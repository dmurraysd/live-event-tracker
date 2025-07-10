package com.dmurraysd.spring.model;

import jakarta.validation.constraints.NotBlank;

public record MatchScore(@NotBlank String eventId, @NotBlank String currentScore) {
}
