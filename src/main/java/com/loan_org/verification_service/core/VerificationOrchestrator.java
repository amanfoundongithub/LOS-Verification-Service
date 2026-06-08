package com.loan_org.verification_service.core;

import com.loan_org.verification_service.api.dto.ProcessRequest;
import com.loan_org.verification_service.api.dto.ProcessResponse;
import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import com.loan_org.verification_service.api.exception.VerificationException;
import com.loan_org.verification_service.core.http.ImageDownloader;
import com.loan_org.verification_service.core.processor.DocumentProcessor;
import com.loan_org.verification_service.core.processor.factory.ProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationOrchestrator {

    private final ProcessorFactory processorFactory;
    private final ImageDownloader imageDownloader;

    public ProcessResponse handleVerification(ProcessRequest request) {

        log.info("Received request to handle verification of document: {} type: {} for applicationId: {}",
                request.storageKey(),
                request.documentType(),
                request.applicationId());

        DocumentProcessor processor = processorFactory.getProcessor(request.documentType())
                .orElseThrow(() -> new VerificationException(
                        String.format("The document type '%s' is not supported for verification.", request.documentType()),
                        VerificationErrorCode.UNSUPPORTED_FORMAT
                ));

        log.info("Downloading image from storage services...");
        BufferedImage image;
        try {
            image = imageDownloader.download(request.downloadUrl());
        } catch (Exception ex) {
            throw new VerificationException(
                    "Network error occurred while fetching document source from storage link.",
                    ex,
                    VerificationErrorCode.DOWNLOAD_FAILED
            );
        }

        if(image == null) {
            throw new VerificationException(
                    "Remote host returned an unparseable or completely empty document payload.",
                    VerificationErrorCode.EMPTY_PAYLOAD
            );
        }
        log.info("Image loaded successfully. Now starting image verification...");

        log.info("Starting image verification of {} for applicationId: {}",
                request.documentType(),
                request.applicationId());
        ProcessResponse response = processor.process(image);
        log.info("Image verification successful for {}. applicationId: {}",
                request.documentType(),
                request.applicationId());
        return response;

    }

}
