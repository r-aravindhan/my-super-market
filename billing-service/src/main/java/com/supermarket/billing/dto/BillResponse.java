package com.supermarket.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private String billId;
    private Map<String, Integer> items;
    private Map<String, BigDecimal> itemPrices;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String currency;
    private String appliedDiscountCode;
}
