package com.loan_org.verification_service.infrastructure.http;

import com.loan_org.verification_service.api.exception.VerificationErrorCode;
import com.loan_org.verification_service.api.exception.VerificationException;
import com.loan_org.verification_service.core.ImageDownloader;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;

@Slf4j
@Component
public class HttpImageDownloader implements ImageDownloader {

    private final RestClient restClient;

    public HttpImageDownloader(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public BufferedImage download(String imageUrl) {
        log.info("Opening RestClient network stream connection to target document URL...");

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new VerificationException(
                    "Cannot request download from an empty or null URL string",
                    VerificationErrorCode.INVALID_STRUCTURE
            );
        }

        try {
            URI targetUri = URI.create(imageUrl);
            Resource documentResource = restClient.get()
                    .uri(targetUri)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .accept(MediaType.APPLICATION_PDF, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.ALL)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Remote document server returned error status code: {}", response.getStatusCode());
                        throw new VerificationException(
                                "Remote storage server failed to serve the document asset. Status code: " + response.getStatusCode(),
                                VerificationErrorCode.DOWNLOAD_FAILED
                        );
                    })
                    .body(Resource.class);

            if (documentResource == null) {
                log.error("HTTP request succeeded, but body stream resource returned an empty payload model.");
                return null;
            }

            try (InputStream inputStream = documentResource.getInputStream()) {

                if (imageUrl.toLowerCase().contains(".pdf")) {
                    log.info("Detected PDF data stream payload format. Initializing Apache PDFBox rendering engine processing loop...");

                    try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(inputStream.readAllBytes())) {
                        if (document.getNumberOfPages() == 0) {
                            log.error("Target PDF document uploaded contains zero readable schema pages.");
                            return null;
                        }

                        log.info("PDF loaded successfully. Rendering page index 0 to a memory matrix buffer...");
                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                        return pdfRenderer.renderImageWithDPI(0, 150);
                    }
                }

                log.info("Fallback handler: Treating byte payload stream as standard image graphics matrix...");
                BufferedImage image = ImageIO.read(inputStream);

                if (image == null) {
                    log.error("Successfully read bytes from server, but the payload format is not a valid image framework matrix.");
                    return null;
                }

                log.info("Successfully fetched regular image asset via RestClient. Dimensions: {}x{}px", image.getWidth(), image.getHeight());
                return image;
            }

        } catch (VerificationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unhandled network or IO processing anomaly occurred while fetching document stream over RestClient.", e);
            throw new VerificationException(
                    "Underlying HTTP asset extraction execution failure over RestClient channel.",
                    e,
                    VerificationErrorCode.DOWNLOAD_FAILED
            );
        }
    }
}