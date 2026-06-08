package com.loan_org.verification_service.api.exception;

import lombok.Getter;

@Getter
public class VerificationException extends RuntimeException {

    private final VerificationErrorCode errorCode;

    public VerificationException(String message, VerificationErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VerificationException(String message, Throwable cause, VerificationErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public VerificationException(Throwable cause, VerificationErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public VerificationException(VerificationErrorCode errorCode) {
        super("Document verification failed with error code: " + errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {

        return String.format("%s [%s]: %s",
                getClass().getName(),
                this.errorCode,
                getLocalizedMessage()
        );

    }

}
