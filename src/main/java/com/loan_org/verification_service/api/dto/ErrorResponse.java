package com.loan_org.verification_service.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        String status,
        VerificationErrorCode errorCode,
        String message,
        Instant timestamp,
        List<String> errors
) {
    public ErrorResponse {
        if(timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
