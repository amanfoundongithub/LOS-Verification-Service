package com.loan_org.verification_service.infrastructure.ocr.impl;

import com.loan_org.verification_service.core.TextExtractor;
import com.loan_org.verification_service.infrastructure.ocr.OcrEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class RuleBasedExtractor implements TextExtractor {

    private final OcrEngine ocrEngine;

    @Override
    public Map<String, String> extract(BufferedImage image) {

        log.info("Starting rule based extraction for the image...");
        String rawText = ocrEngine.extract(image);
        log.debug("Got the text: {}", rawText);

        Map<String, String> fields = new HashMap<>();
        fields.put("rawOcrText", rawText != null ? rawText.trim() : "");
        log.info("Rule based extraction completed successfully");

        return fields;

    }

}
