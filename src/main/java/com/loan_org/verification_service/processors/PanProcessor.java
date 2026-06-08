package com.loan_org.verification_service.processors;


import com.loan_org.verification_service.api.dto.ProcessResponse;
import com.loan_org.verification_service.core.DocumentProcessor;
import com.loan_org.verification_service.core.TextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PanProcessor implements DocumentProcessor {

    private final TextExtractor textExtractor;

    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    @Override
    public String getSupportedDocumentType() {
        return "PAN";
    }

    @Override
    public ProcessResponse process(BufferedImage image) {
        Map<String, Object> resultFields = new HashMap<>();

        Map<String, String> ocrData = textExtractor.extract(image);
        String rawOcrText = ocrData.getOrDefault("rawOcrText", "").toUpperCase();

        Matcher matcher = PAN_PATTERN.matcher(rawOcrText);
        if (matcher.find()) {
            String detectedPan = matcher.group();
            resultFields.put("panCardNumber", detectedPan);
            return new ProcessResponse(true, "PAN card verification completed. Valid text signature pattern isolated.", resultFields);
        }

        return new ProcessResponse(false, "PAN card verification failed: Could not parse a valid 10-character alphanumeric PAN structural code from image layout text.", Map.of());
    }
}