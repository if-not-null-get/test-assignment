package com.ingemark.testassignment.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.testassignment.product.exception.CurrencyServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void whenBankReturnsBadResponse_thenCurrencyServiceExceptionIsThrown() {
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("invalid json");

        assertThrows(CurrencyServiceException.class, () -> currencyService.getEurToUsdRate());
    }

    @Test
    void whenBankReturnsEmptyResponse_thenThrowsCurrencyServiceException() {
        when(restTemplate.getForObject(any(URI.class), eq(String.class))).thenReturn(null);
        assertThrows(CurrencyServiceException.class, () -> currencyService.getEurToUsdRate());
    }

    @Test
    void whenBankReturnsInvalidJson_thenThrowsCurrencyServiceException() throws Exception {
        String invalidJson = "not a json";
        when(restTemplate.getForObject(any(URI.class), eq(String.class))).thenReturn(invalidJson);
        when(objectMapper.readTree(anyString())).thenThrow(new JsonProcessingException("Invalid JSON") {});

        assertThrows(CurrencyServiceException.class, () -> currencyService.getEurToUsdRate());
    }

    @Test
    void whenBankReturnsHttpError_thenThrowsCurrencyServiceException() {
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

        assertThrows(CurrencyServiceException.class, () -> currencyService.getEurToUsdRate());
    }
}
