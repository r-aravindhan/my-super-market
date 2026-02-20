package com.supermarket.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.inventory.dto.ProductRequest;
import com.supermarket.inventory.dto.StockCheckRequest;
import com.supermarket.inventory.dto.StockReservationRequest;
import com.supermarket.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .sku("SKU999")
                .name("Integration Test Product")
                .category("Test")
                .quantity(50)
                .reorderThreshold(5)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU999"))
                .andExpect(jsonPath("$.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.quantity").value(50));

        mockMvc.perform(get("/api/v1/products/SKU999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU999"));
    }

    @Test
    void shouldCheckStockAndReserve() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .sku("SKU100")
                .name("Stock Test Product")
                .category("Test")
                .quantity(100)
                .reorderThreshold(10)
                .build();

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());

        StockCheckRequest checkRequest = StockCheckRequest.builder()
                .items(Map.of("SKU100", 50))
                .build();

        mockMvc.perform(post("/api/v1/products/stock/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        StockReservationRequest reserveRequest = StockReservationRequest.builder()
                .reservationId("RES-INT-001")
                .items(Map.of("SKU100", 50))
                .build();

        mockMvc.perform(post("/api/v1/products/stock/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products/SKU100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void shouldReturn404ForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/v1/products/INVALID_SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidProductCreation() throws Exception {
        ProductRequest invalidRequest = ProductRequest.builder()
                .sku("")
                .name("")
                .category("")
                .quantity(-1)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
