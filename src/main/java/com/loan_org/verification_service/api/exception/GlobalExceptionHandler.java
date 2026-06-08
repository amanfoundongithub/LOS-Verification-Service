package com.loan_org.verification_service.api.exception;

import com.loan_org.verification_service.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorResponse> handleVerificationException(
            VerificationException ex
    ) {

        log.error("Error during verification of documents: {}. Error Code: {}",
                ex.toString(),
                ex.getErrorCode());

        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());
        ErrorResponse response = ErrorResponse.builder()
                .status("FAILED")
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, status);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {

        List<String> detailedErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Error: HTTP structure failure: {}", detailedErrors);

        ErrorResponse response = ErrorResponse.builder()
                .status("BAD_REQUEST")
                .errorCode(VerificationErrorCode.INVALID_STRUCTURE)
                .message("Inbound JSON schema structural validation failed.")
                .errors(detailedErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled error occurred. Reason: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .status("ERROR")
                .errorCode(VerificationErrorCode.INTERNAL_SYSTEM_FAILURE)
                .message("An unexpected exception occurred during the execution of document verification.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private HttpStatus mapErrorCodeToStatus(VerificationErrorCode errorCode) {
        return switch (errorCode) {
            case DOWNLOAD_FAILED -> HttpStatus.BAD_GATEWAY;
            case EMPTY_PAYLOAD, UNSUPPORTED_FORMAT -> HttpStatus.UNPROCESSABLE_ENTITY;
            case INVALID_STRUCTURE -> HttpStatus.BAD_REQUEST;
            case FRAUD_SUSPECTED -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
