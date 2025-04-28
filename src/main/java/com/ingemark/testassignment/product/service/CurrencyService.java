package com.ingemark.testassignment.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.testassignment.product.exception.CurrencyServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@Service
public class CurrencyService {

    @Value("${currency.api.scheme}")
    private String scheme;

    @Value("${currency.api.host}")
    private String host;

    @Value("${currency.api.path}")
    private String path;

    @Value("${currency.api.default-currency}")
    private String defaultCurrency;

    private static final String FIELD_NAME_RATE = "srednji_tecaj";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BigDecimal getEurToUsdRate() {
        try {
            URI url = UriComponentsBuilder.newInstance()
                    .scheme(scheme)
                    .host(host)
                    .path(path)
                    .queryParam("valuta", defaultCurrency)
                    .build()
                    .toUri();

            String response = restTemplate.getForObject(url, String.class);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isArray() && !jsonNode.isEmpty()) {
                String rateString = jsonNode.get(0).get(FIELD_NAME_RATE).asText();
                rateString = rateString.replace(",", ".");

                return new BigDecimal(rateString);
            } else {
                throw new CurrencyServiceException("Invalid response from HNB API: empty or wrong format",
                        HttpStatus.BAD_GATEWAY);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CurrencyServiceException("Failed to reach HNB API: " + e.getMessage(), e, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            throw new CurrencyServiceException(
                    "Unexpected error during currency conversion: " + e.getMessage(),
                    e,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
