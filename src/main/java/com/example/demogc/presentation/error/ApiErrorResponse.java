package com.example.demogc.presentation.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Formato estandar de error de la API")
public record ApiErrorResponse(
        @Schema(example = "2026-03-21T22:00:00Z")
        Instant timestamp,
        @Schema(example = "400")
        int status,
        @Schema(example = "Bad Request")
        String error,
        @Schema(example = "Validation error")
        String message,
        @Schema(example = "/api/users/me")
        String path,
        Map<String, String> validationErrors
) {
}
