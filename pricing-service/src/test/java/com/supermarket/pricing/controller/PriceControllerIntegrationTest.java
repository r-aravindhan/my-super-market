package com.supermarket.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.pricing.dto.PriceCalculationRequest;
import com.supermarket.pricing.dto.PriceRequest;
import com.supermarket.pricing.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("PriceController Integration Tests")
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        priceRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrievePrice() throws Exception {
        PriceRequest request = PriceRequest.builder()
                .sku("SKU999")
                .unitPrice(new BigDecimal("5.99"))
                .currency("USD")
                .build();

        mockMvc.perform(post("/api/v1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU999"))
                .andExpect(jsonPath("$.unitPrice").value(5.99))
                .andExpect(jsonPath("$.currency").value("USD"));

        mockMvc.perform(get("/api/v1/prices/SKU999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU999"));
    }

    @Test
    void shouldCalculatePrices() throws Exception {
        PriceRequest priceRequest = PriceRequest.builder()
                .sku("SKU100")
                .unitPrice(new BigDecimal("10.00"))
                .currency("USD")
                .build();

        mockMvc.perform(post("/api/v1/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(priceRequest)))
                .andExpect(status().isCreated());

        PriceCalculationRequest calcRequest = PriceCalculationRequest.builder()
                .items(Map.of("SKU100", 5))
                .build();

        mockMvc.perform(post("/api/v1/prices/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(50.0))
                .andExpect(jsonPath("$.itemPrices.SKU100").value(50.0))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void shouldReturn404ForNonExistentPrice() throws Exception {
        mockMvc.perform(get("/api/v1/prices/INVALID_SKU"))
                .andExpect(status().isNotFound());
    }
}
