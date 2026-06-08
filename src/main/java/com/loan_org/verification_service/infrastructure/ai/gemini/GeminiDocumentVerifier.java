package com.loan_org.verification_service.infrastructure.ai.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan_org.verification_service.api.dto.DocumentVerifierResult;
import com.loan_org.verification_service.infrastructure.ai.gemini.dto.GeminiRequest;
import com.loan_org.verification_service.infrastructure.ai.gemini.dto.GeminiResponse;
import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import com.loan_org.verification_service.api.exception.VerificationException;
import com.loan_org.verification_service.core.DocumentVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class GeminiDocumentVerifier implements DocumentVerifier {

    private final RestClient   restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key.value}")
    private String apiKey;

    @Value("${gemini.api-key.header}")
    private String apiKeyHeader;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${gemini.prompts.document-verification}")
    private String prompt;

    public GeminiDocumentVerifier(RestClient.Builder restClientBuilder,
                                  ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public DocumentVerifierResult verifyDataMatch(String rawOcrText, Map<String, String> targetFields) {
        log.info("Dispatching verification payload to Gemini API via RestClient...");

        String userPrompt = String.format(prompt, targetFields.toString(), rawOcrText);
        GeminiRequest requestPayload = GeminiRequest.of(userPrompt);

        try {

            GeminiResponse response = restClient.post()
                    .uri(String.format(baseUrl, model))
                    .header(apiKeyHeader, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new VerificationException("Empty response payload dropped by Google AI gateway.", VerificationErrorCode.INTERNAL_SYSTEM_FAILURE);
            }

            String rawJsonText = response.getOutputText();
            log.info("Gemini Analysis executed successfully. Output text captured.");
            return objectMapper.readValue(cleanOutput(rawJsonText), DocumentVerifierResult.class);

        } catch (Exception ex) {

            log.error("Failed to run custom validation check on direct Gemini endpoint channel.", ex);
            throw new VerificationException(
                    "AI processing infrastructure timed out or failed to parse schema.",
                    ex,
                    VerificationErrorCode.INTERNAL_SYSTEM_FAILURE
            );

        }
    }

    private String cleanOutput(String output) {
        if (output != null) {
            output = output.trim();
            if (output.startsWith("```json")) {
                output = output.substring(7, output.lastIndexOf("```")).trim();
            } else if (output.startsWith("```")) {
                output = output.substring(3, output.lastIndexOf("```")).trim();
            }
        }
        return output;
    }
}