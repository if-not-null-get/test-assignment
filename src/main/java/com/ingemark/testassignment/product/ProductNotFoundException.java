package com.ingemark.testassignment.product;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product with ID " + productId + " not found.");
    }
}
