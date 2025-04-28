package com.ingemark.testassignment.product.exception;

import org.springframework.http.HttpStatus;

public class CurrencyServiceException extends RuntimeException {
    private final HttpStatus status;

    public CurrencyServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CurrencyServiceException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
