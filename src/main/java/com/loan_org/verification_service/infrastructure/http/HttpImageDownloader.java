package com.loan_org.verification_service.infrastructure.http;

import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import com.loan_org.verification_service.api.exception.VerificationException;
import com.loan_org.verification_service.core.ImageDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Slf4j
@Component
public class HttpImageDownloader implements ImageDownloader {

    @Override
    public BufferedImage download(String imageUrl) {

        log.info("Opening network stream connection to {}...", imageUrl);
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new VerificationException(
                    "Cannot request download from an empty or null URL string",
                    VerificationErrorCode.INVALID_STRUCTURE
            );
        }

        try {
            URL url = URI.create(imageUrl).toURL();

            try (InputStream inputStream = url.openStream()) {
                BufferedImage image = ImageIO.read(inputStream);

                if (image == null) {
                    log.error("Successfully read bytes from server, but the payload format is not a valid image framework matrix.");
                    return null;
                }

                log.info("Successfully fetched image. Dimensions: {}x{}px", image.getWidth(), image.getHeight());
                return image;

            }

        } catch (Exception e) {

            log.error("Failed to acquire connection or read document binary data from remote target endpoint.");
            throw new VerificationException("Underlying HTTP extraction failure", e, VerificationErrorCode.DOWNLOAD_FAILED);
        }

    }
}
