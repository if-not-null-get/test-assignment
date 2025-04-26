package com.ingemark.testassignment.product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        var product = Product.fromRequest(request);

        UUID savedProductId = productRepository.save(product);
        var savedProduct = productRepository.findById(savedProductId)
                .orElseThrow(() -> new ProductNotFoundException(savedProductId));

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
