package com.loan_org.verification_service.core;

import com.loan_org.verification_service.api.dto.ProcessResponse;

import java.awt.image.BufferedImage;

public interface DocumentProcessor {
    ProcessResponse process(BufferedImage image);
    String getSupportedDocumentType();
}
