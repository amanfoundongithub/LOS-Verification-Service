package com.loan_org.verification_service.infrastructure.ocr.impl;

import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import com.loan_org.verification_service.api.exception.VerificationException;
import com.loan_org.verification_service.infrastructure.ocr.OcrEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Slf4j
@RequiredArgsConstructor
@Component
public class TesseractOcrEngine implements OcrEngine {

    private final Tesseract tesseract;

    @Override
    public String extract(BufferedImage image) {

        log.info("Performing OCR extraction on image...");
        if(image == null) {
            throw new VerificationException(
                    "Cannot perform OCR processing on a null image",
                    VerificationErrorCode.EMPTY_PAYLOAD);
        }

        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new VerificationException(
                    "Tesseract failure during OCR:" + e.getMessage(), e,
                    VerificationErrorCode.OCR_PARSING_ERROR);
        }

    }

}
