package com.ingemark.testassignment.product.service;

import com.ingemark.testassignment.product.AbstractIntegrationTest;
import com.ingemark.testassignment.product.dto.ProductRequest;
import com.ingemark.testassignment.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
public class ProductServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void givenValidRequest_whenCreateProduct_thenProductIsCreated() {
        var request = new ProductRequest(
                "1234567890",
                "Test Product",
                new BigDecimal("99.99"),
                true
        );

        var response = productService.createProduct(request);

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(request.code());
        assertThat(response.name()).isEqualTo(request.name());
        assertThat(response.priceEur()).isEqualByComparingTo(request.priceEur());
        assertThat(response.isAvailable()).isEqualTo(request.isAvailable());
        assertThat(response.priceUsd()).isNotNull();
    }

    @Test
    void givenExistingProduct_whenGetProductById_thenReturnsProduct() {
        var request = new ProductRequest(
                "ABCDEFGHIJ",
                "Another Product",
                new BigDecimal("199.99"),
                true
        );

        var created = productService.createProduct(request);
        var fetched = productService.getProductById(created.id());

        assertThat(fetched).isNotNull();
        assertThat(fetched.id()).isEqualTo(created.id());
    }

    @Test
    void whenGetAllProducts_thenReturnsList() {
        ProductRequest request1 = new ProductRequest(
                "1111111111",
                "First Product",
                new BigDecimal("10.00"),
                true
        );

        var request2 = new ProductRequest(
                "2222222222",
                "Second Product",
                new BigDecimal("20.00"),
                true
        );

        productService.createProduct(request1);
        productService.createProduct(request2);

        List<ProductResponse> products = productService.getAllProducts();

        assertThat(products.size()).isGreaterThanOrEqualTo(2);
    }
}
