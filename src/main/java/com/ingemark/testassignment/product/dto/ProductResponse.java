package com.ingemark.testassignment.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse (UUID id,
                               String code,
                               String name,
                               BigDecimal priceEur,
                               BigDecimal priceUsd,
                               boolean isAvailable
) {}
