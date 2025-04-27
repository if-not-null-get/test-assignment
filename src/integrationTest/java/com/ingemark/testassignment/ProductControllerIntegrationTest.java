package com.ingemark.testassignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.testassignment.product.ProductRequest;
import com.ingemark.testassignment.product.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final String BASE_URL = "/products";

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM products");
    }

    @Test
    void givenValidRequest_whenCreateProduct_thenReturnsProduct() throws Exception {
        var request = new ProductRequest(
                "ABCDEFGHIJ",
                "Test Product",
                new BigDecimal("99.99"),
                true
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value(request.code()))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.priceEur").value(request.priceEur()))
                .andExpect(jsonPath("$.isAvailable").value(request.isAvailable()));
    }

    @Test
    void givenExistingProduct_whenGetById_thenReturnsProduct() throws Exception {
        var request = new ProductRequest(
                "1234567890",
                "Another Product",
                new BigDecimal("59.99"),
                true
        );

        String content = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdProduct = objectMapper.readValue(content, ProductResponse.class);

        mockMvc.perform(get(BASE_URL + "/" + createdProduct.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.id().toString()))
                .andExpect(jsonPath("$.code").value(createdProduct.code()))
                .andExpect(jsonPath("$.name").value(createdProduct.name()));
    }

    @Test
    void whenGetAllProducts_thenReturnsProductList() throws Exception {
        var request1 = new ProductRequest(
                "0000000001",
                "First Product",
                new BigDecimal("10.00"),
                true
        );

        var request2 = new ProductRequest(
                "0000000002",
                "Second Product",
                new BigDecimal("20.00"),
                true
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void givenShortCode_whenCreateProduct_thenBadRequestReturned() throws Exception {
        var request = new ProductRequest(
                "SHORT",
                "Invalid Product",
                new BigDecimal("10.00"),
                true
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNegativePrice_whenCreateProduct_thenBadRequestReturned() throws Exception {
        var request = new ProductRequest(
                "1234567890",
                "Invalid Price Product",
                new BigDecimal("-10.00"),
                true
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNonExistingId_whenGetProduct_thenNotFoundReturned() throws Exception {
        mockMvc.perform(get("/products/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDuplicateCode_whenCreateProduct_thenBadRequestReturned() throws Exception {
        var request = new  ProductRequest(
                "DUPLICATE1",
                "First Product",
                new BigDecimal("10.00"),
                true
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenEmptyRequest_whenCreateProduct_thenBadRequestReturned() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
