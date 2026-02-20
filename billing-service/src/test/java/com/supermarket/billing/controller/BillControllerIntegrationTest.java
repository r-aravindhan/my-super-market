package com.supermarket.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.billing.client.DiscountsClient;
import com.supermarket.billing.client.InventoryClient;
import com.supermarket.billing.client.PricingClient;
import com.supermarket.billing.dto.BillRequest;
import com.supermarket.billing.dto.DiscountApplicationResponse;
import com.supermarket.billing.dto.PriceCalculationResponse;
import com.supermarket.billing.dto.StockCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("BillController Integration Tests")
class BillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryClient inventoryClient;

    @MockBean
    private PricingClient pricingClient;

    @MockBean
    private DiscountsClient discountsClient;

    @Test
    void shouldCreateBillSuccessfully() throws Exception {
        BillRequest request = BillRequest.builder()
                .items(Map.of("SKU001", 2, "SKU002", 1))
                .discountCode("SAVE10")
                .build();

        when(inventoryClient.checkStock(any())).thenReturn(StockCheckResponse.builder().available(true).build());
        when(pricingClient.calculatePrices(any())).thenReturn(PriceCalculationResponse.builder()
                .itemPrices(Map.of("SKU001", new BigDecimal("5.98"), "SKU002", new BigDecimal("3.49")))
                .subtotal(new BigDecimal("9.47"))
                .currency("USD")
                .build());
        when(discountsClient.applyDiscount(any())).thenReturn(DiscountApplicationResponse.builder()
                .discountAmount(new BigDecimal("0.95"))
                .totalAfterDiscount(new BigDecimal("8.52"))
                .appliedDiscountCode("SAVE10")
                .build());

        mockMvc.perform(post("/api/v1/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.billId").exists())
                .andExpect(jsonPath("$.total").value(8.52))
                .andExpect(jsonPath("$.subtotal").value(9.47))
                .andExpect(jsonPath("$.discountAmount").value(0.95))
                .andExpect(jsonPath("$.appliedDiscountCode").value("SAVE10"));
    }

    @Test
    void shouldReturn400ForInvalidBillRequest() throws Exception {
        BillRequest invalidRequest = BillRequest.builder()
                .items(Map.of())
                .build();

        mockMvc.perform(post("/api/v1/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInsufficientStock() throws Exception {
        BillRequest request = BillRequest.builder()
                .items(Map.of("SKU001", 1000))
                .build();

        when(inventoryClient.checkStock(any())).thenReturn(StockCheckResponse.builder()
                .available(false)
                .insufficientItems(Map.of("SKU001", "Requested: 1000, Available: 10"))
                .build());

        mockMvc.perform(post("/api/v1/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
