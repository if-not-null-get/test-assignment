package com.ingemark.testassignment;

import com.ingemark.testassignment.product.Product;
import com.ingemark.testassignment.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Testcontainers
@Import(ProductRepository.class)
public class ProductRepositoryIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13-alpine")
            .withDatabaseName("test_assignment")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void givenValidProduct_whenSave_thenFindByCodeReturnsProduct() {
        var product = createProduct(
                "Test Product",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true);

        productRepository.save(product);

        var result = productRepository.findByCode(product.getCode());

        assertThat(result).isPresent().hasValueSatisfying(
                value -> assertProductsEqual(value, product)
        );
    }

    @Test
    void givenValidProduct_whenFindById_thenReturnsProduct() {
        var product = createProduct(
                "Test Product",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true);

        var saved = productRepository.save(product);
        var result = productRepository.findById(saved);

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
