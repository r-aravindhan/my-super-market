package com.supermarket.discounts.dto;

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
public class DiscountApplicationResponse {

    private BigDecimal discountAmount;
    private BigDecimal totalAfterDiscount;
    private String appliedDiscountCode;
    private Map<String, BigDecimal> itemDiscounts;
}
