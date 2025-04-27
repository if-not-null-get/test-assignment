package com.ingemark.testassignment.product.repository;

import com.ingemark.testassignment.product.AbstractIntegrationTest;
import com.ingemark.testassignment.product.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Testcontainers
@Import(ProductRepository.class)
public class ProductRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void givenValidProduct_whenSave_thenFindByCodeReturnsProduct() {
        var product = createProduct(
                "Test Product",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true);

        var savedProduct = productRepository.save(product);
        var result = productRepository.findByCode(product.getCode());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(value -> {
                    assertProductsEqual(value, product);
                    assertThat(value.getId()).isEqualTo(savedProduct.getId());
                }
        );
    }

    @Test
    void givenValidProduct_whenFindById_thenReturnsProduct() {
        var product = createProduct(
                "Test Product",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true);

        var savedProduct = productRepository.save(product);
        var result = productRepository.findById(savedProduct.getId());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(value -> assertProductsEqual(value, product));
    }

    @Test
    void givenValidProducts_whenFindAll_thenReturnsAllProducts() {
        var firstProduct = createProduct(
                "Test Product_1",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true);
        var secondProduct = createProduct(
                "Test Product_2",
                new BigDecimal("50.00"),
                new BigDecimal("53.10"),
                false);

        productRepository.save(firstProduct);
        productRepository.save(secondProduct);

        List<Product> products = productRepository.findAll();

        assertThat(products)
                .isNotEmpty()
                .extracting(Product::getCode)
                .contains(firstProduct.getCode(), secondProduct.getCode());
    }

    private Product createProduct(String name, BigDecimal priceEur, BigDecimal priceUsd, boolean isAvailable) {
        return new Product(
                null,
                UUID.randomUUID().toString().substring(0, 10),
                name,
                priceEur,
                priceUsd,
                isAvailable
        );
    }

    private void assertProductsEqual(Product actual, Product expected) {
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPriceEur()).isEqualByComparingTo(expected.getPriceEur());
        assertThat(actual.getPriceUsd()).isEqualByComparingTo(expected.getPriceUsd());
        assertThat(actual.isAvailable()).isEqualTo(expected.isAvailable());
    }
}
