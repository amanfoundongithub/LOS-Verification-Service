package com.loan_org.verification_service.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class TesseractConfig {

    @Value("${tesseract.data.path}")
    private String dataPath;

    @Value("${tesseract.data.language}")
    private String language;

    @Value("${tesseract.jna.path:}")
    private String jnaPath;

    @Bean
    public Tesseract tesseract() {

        log.info("Initializing Tesseract OCR with language: {}", language);
        if(StringUtils.hasText(jnaPath)) {
            log.info("Overriding default JNA Path with: {}", jnaPath);
            System.setProperty("jna.library.path", jnaPath);
        }

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage(language);
        tesseract.setOcrEngineMode(1);
        log.info("Successfully initialized Tesseract OCR!");

        return tesseract;
    }

}
