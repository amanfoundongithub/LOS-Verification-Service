package com.loan_org.verification_service.infrastructure.ai.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(List<Candidate> candidates) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {

    }

    public String getOutputText() {
        if (candidates != null && !candidates.isEmpty() && candidates.getFirst().content() != null) {
            List<Part> parts = candidates.getFirst().content().parts();
            if (parts != null && !parts.isEmpty()) {
                return parts.getFirst().text();
            }
        }
        return "";
    }
}
