package com.loan_org.verification_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProcessRequest(

        @NotBlank(message = "documentId associated with Loan is required.")
        String documentId,

        @NotBlank(message = "storageKey associated with the document is required to be linked to this verification.")
        String storageKey,

        @NotBlank(message = "Document download URL is required to download the document.")
        String downloadUrl,

        @NotBlank(message = "Please provide the document type (e.g. AADHAAR, PAN) so that we can do analysis accordingly.")
        String documentType

) {}
