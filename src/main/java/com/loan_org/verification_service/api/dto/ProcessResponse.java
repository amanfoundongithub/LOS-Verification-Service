package com.loan_org.verification_service.api.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ProcessResponse(
        boolean verified,
        String message,
        Map<String, Object> extractedData
) {}