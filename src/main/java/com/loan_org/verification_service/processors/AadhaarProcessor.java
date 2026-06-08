package com.loan_org.verification_service.processors;


import com.loan_org.verification_service.api.dto.DocumentVerifierResult;
import com.loan_org.verification_service.api.dto.ProcessResponse;
import com.loan_org.verification_service.core.DocumentVerifier;
import com.loan_org.verification_service.core.DocumentProcessor;
import com.loan_org.verification_service.core.TextExtractor;
import com.loan_org.verification_service.core.QrReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AadhaarProcessor implements DocumentProcessor {

    private final TextExtractor textExtractor;
    private final QrReader      qrReader;
    private final DocumentVerifier aiAnalyzer;

    @Override
    public String getSupportedDocumentType() {
        return "AADHAAR";
    }

    @Override
    public ProcessResponse process(BufferedImage image) {

        log.info("Processing AADHAAR verification...");
        Map<String, Object> responseData = new HashMap<>();

        log.info("Preparing for OCR extraction...");
        Map<String, String> ocrResult = textExtractor.extract(image);
        String rawOcrText = ocrResult.getOrDefault("rawOcrText", "").toUpperCase();

        log.info("OCR success! Now reading QR code....");
        Optional<String> qrDataOpt = qrReader.read(image);
        if (qrDataOpt.isEmpty()) {
            log.warn("QR code not detected in the picture. Could not verify authenticity. Aborted.");
            return new ProcessResponse(false,
                    "Aadhaar validation failed: QR code could not be detected on the document picture.",
                    Map.of());
        }

        String qrData = qrDataOpt.get();
        responseData.put("qrCodeDetected", true);
        log.info("QR reading successful! Now validating the details...");

        // DEBUG statements; will be removed later
        responseData.put("rawdata", rawOcrText);
        responseData.put("qrdata", qrData);

        Map<String, String> targetFields = Map.of(
                "documentType", "AADHAAR",
                "rawQrData", qrDataOpt.get()
        );

        DocumentVerifierResult aiResult = aiAnalyzer.verifyDataMatch(rawOcrText, targetFields);

        responseData.put("aiConfidence", aiResult.confidenceScore());
        responseData.put("aiReasoning", aiResult.reasoning());
        responseData.put("flaggedAnomalies", aiResult.flaggedAnomalies());

        String matchMessage = aiResult.isMatch()
                ? "Aadhaar verified successfully by AI analysis engine."
                : "AI flagged structural discrepancy or data validation mismatch: " + aiResult.flaggedAnomalies();

        return new ProcessResponse(aiResult.isMatch(), matchMessage, responseData);
    }

}