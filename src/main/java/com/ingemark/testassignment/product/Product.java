package com.ingemark.testassignment.product;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private final UUID id;
    private final String code;
    private final String name;
    private final BigDecimal priceEur;
    private final BigDecimal priceUsd;
    private final boolean isAvailable;

    public Product(String code, String name, BigDecimal priceEur, BigDecimal priceUsd, boolean isAvailable) {
        this(null, code, name, priceEur, priceUsd, isAvailable);
    }

    public Product(UUID id, String code, String name, BigDecimal priceEur,
                   BigDecimal priceUsd, boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.priceEur = priceEur;
        this.priceUsd = priceUsd;
        this.isAvailable = isAvailable;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPriceEur() {
        return priceEur;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public static Product fromRequest(ProductRequest dto) {
        return new Product(
                dto.code(),
                dto.name(),
                dto.priceEur(),
                BigDecimal.ZERO,
                dto.isAvailable()
        );
    }

    public ProductResponse toResponse() {
        return new ProductResponse(
                this.id,
                this.code,
                this.name,
                this.priceEur,
                this.priceUsd,
                this.isAvailable
        );
    }
}
