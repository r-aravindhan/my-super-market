package com.supermarket.discounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.discounts.dto.DiscountApplicationRequest;
import com.supermarket.discounts.dto.DiscountRequest;
import com.supermarket.discounts.entity.Discount;
import com.supermarket.discounts.repository.DiscountRepository;
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
import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("DiscountController Integration Tests")
class DiscountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiscountRepository discountRepository;

    @BeforeEach
    void setUp() {
        discountRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveDiscount() throws Exception {
        DiscountRequest request = DiscountRequest.builder()
                .code("TEST10")
                .description("Test 10% off")
                .type(Discount.DiscountType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusMonths(1))
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TEST10"))
                .andExpect(jsonPath("$.type").value("PERCENTAGE"))
                .andExpect(jsonPath("$.value").value(10));

        mockMvc.perform(get("/api/v1/discounts/TEST10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST10"));
    }

    @Test
    void shouldApplyDiscount() throws Exception {
        DiscountRequest discountRequest = DiscountRequest.builder()
                .code("SAVE10")
                .description("10% off")
                .type(Discount.DiscountType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .minPurchaseAmount(new BigDecimal("50"))
                .validFrom(LocalDate.now().minusDays(1))
                .validTo(LocalDate.now().plusMonths(1))
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discountRequest)))
                .andExpect(status().isCreated());

        DiscountApplicationRequest applyRequest = DiscountApplicationRequest.builder()
                .items(Map.of("SKU001", 5))
                .itemPrices(Map.of("SKU001", new BigDecimal("100")))
                .subtotal(new BigDecimal("100"))
                .discountCode("SAVE10")
                .build();

        mockMvc.perform(post("/api/v1/discounts/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountAmount").value(10.0))
                .andExpect(jsonPath("$.totalAfterDiscount").value(90.0))
                .andExpect(jsonPath("$.appliedDiscountCode").value("SAVE10"));
    }

    @Test
    void shouldReturn404ForNonExistentDiscount() throws Exception {
        mockMvc.perform(get("/api/v1/discounts/INVALID"))
                .andExpect(status().isNotFound());
    }
}
