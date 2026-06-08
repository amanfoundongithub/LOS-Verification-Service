package com.loan_org.verification_service.api;


import com.loan_org.verification_service.api.dto.ProcessRequest;
import com.loan_org.verification_service.api.dto.ProcessResponse;
import com.loan_org.verification_service.core.VerificationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verify")
public class VerificationController {

    private final VerificationOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<ProcessResponse> verifyDocument(@Validated @RequestBody ProcessRequest request) {
        ProcessResponse outcome = orchestrator.handleVerification(request);
        return ResponseEntity.ok(outcome);
    }
}