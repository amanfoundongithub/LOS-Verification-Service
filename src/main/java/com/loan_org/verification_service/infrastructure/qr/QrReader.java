package com.loan_org.verification_service.infrastructure.qr;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface QrReader {
    Optional<String> read(BufferedImage image);
}
