package com.ingemark.testassignment.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(UUID productId) {
        super("Product with ID " + productId + " not found.");
    }
}
