package com.ingemark.testassignment.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbc;

    private static final String SAVE = "INSERT INTO products (code, name, price_eur, price_usd, is_available) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String GET_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM products";
    private static final String GET_BY_CODE = "SELECT * FROM products WHERE code = ?";

    public ProductRepository(JdbcTemplate template) {
        this.jdbc = template;
    }

    public UUID save(Product product) {
        return jdbc.queryForObject(SAVE, (rs, rowNum) -> rs.getObject("id", UUID.class), product.getCode(), product.getName(), product.getPriceEur(), product.getPriceUsd(), product.isAvailable());
    }

    public Optional<Product> findById(UUID id) {
        return jdbc.query(GET_BY_ID, rowMapper(), id).stream().findFirst();
    }

    public List<Product> findAll() {
        return jdbc.query(GET_ALL, rowMapper());
    }

    public Optional<Product> findByCode(String code) {
        return jdbc.query(GET_BY_CODE, rowMapper(), code).stream().findFirst();
    }

    private RowMapper<Product> rowMapper() {
        return (rs, rowNum) -> new Product(
                UUID.fromString(rs.getString("id")),
                rs.getString("code"),
                rs.getString("name"),
                rs.getBigDecimal("price_eur"),
                rs.getBigDecimal("price_usd"),
                rs.getBoolean("is_available")
        );
    }
}
