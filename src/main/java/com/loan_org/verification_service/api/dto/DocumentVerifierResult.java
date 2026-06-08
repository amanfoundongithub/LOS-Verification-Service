package com.loan_org.verification_service.api.dto;

public record DocumentVerifierResult(
        boolean isMatch,
        int confidenceScore,
        String reasoning,
        String flaggedAnomalies
) {}