package com.loan_org.verification_service.core.http;

import java.awt.image.BufferedImage;

public interface ImageDownloader {
    BufferedImage download(String imageUrl);
}
