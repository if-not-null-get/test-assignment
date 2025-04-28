package com.ingemark.testassignment.product.service;

import com.ingemark.testassignment.product.dto.ProductRequest;
import com.ingemark.testassignment.product.dto.ProductResponse;
import com.ingemark.testassignment.product.exception.DuplicateCodeException;
import com.ingemark.testassignment.product.exception.ProductNotFoundException;
import com.ingemark.testassignment.product.model.Product;
import com.ingemark.testassignment.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CurrencyService currencyService;
    @InjectMocks
    private ProductService productService;

    @Test
    void givenValidRequest_whenCreateProduct_thenSavesProduct() {
        var request = new ProductRequest(
                "1234567890",
                "Test Product",
                BigDecimal.TEN,
                true
        );

        var product = new Product(
                UUID.randomUUID(),
                request.code(),
                request.name(),
                request.priceEur(),
                BigDecimal.ZERO,
                request.isAvailable()
        );

        when(productRepository.findByCode(request.code())).thenReturn(Optional.empty());
        when(currencyService.getEurToUsdRate()).thenReturn(BigDecimal.valueOf(1.1));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.createProduct(request);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void givenDuplicateCode_whenCreateProduct_thenThrowsDuplicateCodeException() {
        var request = new ProductRequest(
                "1234567890", "Test Product", BigDecimal.TEN, true
        );

        when(productRepository.findByCode(request.code()))
                .thenReturn(Optional.of(mock(Product.class)));

        assertThrows(DuplicateCodeException.class, () -> productService.createProduct(request));
    }

    @Test
    void givenExistingProductId_whenGetProduct_thenReturnsProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(
                id,
                "1234567890",
                "Test Product",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(11),
                true
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        var response = productService.getProductById(id);

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(product.getCode());
        assertThat(response.name()).isEqualTo(product.getName());
        assertThat(response.priceEur()).isEqualByComparingTo(product.getPriceEur());
        assertThat(response.priceUsd()).isEqualByComparingTo(product.getPriceUsd());
        assertThat(response.isAvailable()).isEqualTo(product.isAvailable());
    }

    @Test
    void givenNonExistingProductId_whenGetProduct_thenThrowsProductNotFoundException() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));
    }

    @Test
    void whenGetAllProducts_thenReturnsList() {
        when(productRepository.findAll()).thenReturn(List.of(mock(Product.class), mock(Product.class)));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
    }
}
