package com.supermarket.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountApplicationRequest {

    private Map<String, Integer> items;
    private Map<String, BigDecimal> itemPrices;
    private BigDecimal subtotal;
    private String discountCode;
}
