package com.loan_org.verification_service.core.ocr;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface TextExtractor {
    Map<String, String> extract(BufferedImage image);
}