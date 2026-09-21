package br.com.fiap.aguiabranca.ai.api.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateInsightRequest(
        Instant from,
        Instant to
) {
}
