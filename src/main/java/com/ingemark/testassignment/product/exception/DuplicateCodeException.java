package com.ingemark.testassignment.product.exception;

public class DuplicateCodeException extends ProductException {
    public DuplicateCodeException(String code) {
        super("Product with code " + code + " already exists");
    }
}
