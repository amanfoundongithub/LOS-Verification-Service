package com.loan_org.verification_service.infrastructure.ocr;

import java.awt.image.BufferedImage;

public interface OcrEngine {
    String extract(BufferedImage image);
}