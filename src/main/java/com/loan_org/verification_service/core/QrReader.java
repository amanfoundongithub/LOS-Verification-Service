package com.loan_org.verification_service.core;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface QrReader {
    Optional<String> read(BufferedImage image);
}
