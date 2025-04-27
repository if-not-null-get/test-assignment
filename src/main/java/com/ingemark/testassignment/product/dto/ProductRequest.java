package com.ingemark.testassignment.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest (

        @NotNull
        @Size(min = 10, max = 10)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal priceEur,

        boolean isAvailable
) {}
