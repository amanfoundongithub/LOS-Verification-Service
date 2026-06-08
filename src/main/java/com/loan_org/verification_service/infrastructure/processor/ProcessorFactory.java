package com.loan_org.verification_service.infrastructure.processor;

import com.loan_org.verification_service.core.DocumentProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProcessorFactory {

    private final Map<String, DocumentProcessor> processorRegistry;

    public ProcessorFactory(List<DocumentProcessor> processors) {
        this.processorRegistry = processors.stream()
                .collect(Collectors.toMap(
                        processor -> processor.getSupportedDocumentType().toUpperCase(),
                        Function.identity()
                ));
    }

    public Optional<DocumentProcessor> getProcessor(String documentType) {
        if (documentType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(processorRegistry.get(documentType.toUpperCase()));
    }

}
