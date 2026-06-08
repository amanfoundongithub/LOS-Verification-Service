package com.loan_org.verification_service.api.exception;

public enum VerificationErrorCode {
    DOWNLOAD_FAILED,
    EMPTY_PAYLOAD,
    UNSUPPORTED_FORMAT,
    OCR_PARSING_ERROR,
    INVALID_STRUCTURE,
    FRAUD_SUSPECTED
}