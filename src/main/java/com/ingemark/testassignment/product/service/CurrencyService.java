package com.ingemark.testassignment.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class CurrencyService {
    private static final String HNB_API_URL = "https://api.hnb.hr/tecajn-eur/v3";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BigDecimal getEurToUsdRate() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(HNB_API_URL)
                    .queryParam("valuta", "USD")
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isArray() && !jsonNode.isEmpty()) {
                String rateString = jsonNode.get(0).get("srednji_tecaj").asText();
                rateString = rateString.replace(",", ".");

                return new BigDecimal(rateString);
            } else {
                throw new RuntimeException("Invalid response from HNB API: empty or wrong format");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch exchange rate from HNB API", e);
        }
    }
}
