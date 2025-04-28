package com.ingemark.testassignment.product.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ProductExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateCodeException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DuplicateCodeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CurrencyServiceException.class)
    public ResponseEntity<String> handleCurrencyServiceException(CurrencyServiceException ex) {
        log.error("Currency service error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getMessage());
    }
}
