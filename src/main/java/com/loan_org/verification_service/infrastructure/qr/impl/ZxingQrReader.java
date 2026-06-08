package com.loan_org.verification_service.infrastructure.qr.impl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.loan_org.verification_service.infrastructure.qr.QrReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ZxingQrReader implements QrReader {

    @Override
    public Optional<String> read(BufferedImage image) {

        log.info("Reading QR code using Zxing...");
        if(image == null) {
            log.warn("No image detected; cannot read QR. Aborting.");
            return Optional.empty();
        }

        try {

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            log.info("QR code read successfully.");
            return Optional.ofNullable(result.getText());

        } catch (NotFoundException e) {
            log.error("Error during reading of QR code: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
