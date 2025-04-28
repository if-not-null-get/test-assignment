package com.ingemark.testassignment.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.testassignment.product.exception.CurrencyServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BigDecimal getEurToUsdRate() {
        log.info("Fetching EUR to USD exchange rate from HNB API");
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
                String rate = jsonNode.get(0).get(FIELD_NAME_RATE).asText();
                rate = rate.replace(",", ".");

                log.info("Successfully fetched exchange rate: {}", rate);
                return new BigDecimal(rate);
            } else {
                String errorMessage = "Invalid response from HNB API: empty or wrong format";
                log.error(errorMessage);
                throw new CurrencyServiceException(errorMessage, HttpStatus.BAD_GATEWAY);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = "Failed to reach HNB API: " + e.getMessage();
            log.error(errorMessage, e);
            throw new CurrencyServiceException(errorMessage, e, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            String errorMessage = "Unexpected error during currency conversion: " + e.getMessage();
            log.error(errorMessage, e);
            throw new CurrencyServiceException(errorMessage, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
