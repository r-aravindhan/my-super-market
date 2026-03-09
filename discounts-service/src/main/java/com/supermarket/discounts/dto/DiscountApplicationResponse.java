package com.supermarket.discounts.dto;

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
public class DiscountApplicationResponse {

    private BigDecimal discountAmount;
    private BigDecimal totalAfterDiscount;
    private String appliedDiscountCode;
    private Map<String, BigDecimal> itemDiscounts;
}
