package com.loan_org.verification_service.core;

import com.loan_org.verification_service.api.dto.DocumentVerifierResult;

import java.util.Map;

public interface DocumentVerifier {
    DocumentVerifierResult verifyDataMatch(String rawOcrText, Map<String, String> targetFields);
}