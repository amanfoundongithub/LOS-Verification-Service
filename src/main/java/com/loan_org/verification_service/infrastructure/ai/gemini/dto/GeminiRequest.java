package com.loan_org.verification_service.infrastructure.ai.gemini.dto;

import java.util.List;

public record GeminiRequest(List<Content> contents) {

    public record Content(List<Part> parts) {

    }

    public record Part(String text) {

    }

    public static GeminiRequest of(String promptText) {
        return new GeminiRequest(List.of(new Content(List.of(new Part(promptText)))));
    }
}