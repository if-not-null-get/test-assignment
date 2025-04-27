package com.ingemark.testassignment.product.service;

import com.ingemark.testassignment.product.repository.ProductRepository;
import com.ingemark.testassignment.product.dto.ProductRequest;
import com.ingemark.testassignment.product.dto.ProductResponse;
import com.ingemark.testassignment.product.exception.DuplicateCodeException;
import com.ingemark.testassignment.product.exception.ProductNotFoundException;
import com.ingemark.testassignment.product.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CurrencyService currencyService;

    public ProductService(ProductRepository productRepository, CurrencyService currencyService) {
        this.productRepository = productRepository;
        this.currencyService = currencyService;
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findByCode(request.code()).isPresent()) {
            throw new DuplicateCodeException(request.code());
        }

        BigDecimal eurToUsdRate = currencyService.getEurToUsdRate();

        var product = Product.fromRequest(request, request.priceEur().multiply(eurToUsdRate));
        var savedProduct = productRepository.save(product);

        return savedProduct.toResponse();
    }

    public ProductResponse getProductById(UUID id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return product.toResponse();
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(Product::toResponse)
                .toList();
    }
}
