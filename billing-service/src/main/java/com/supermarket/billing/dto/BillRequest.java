package com.supermarket.billing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "At least one item is required")
    private Map<String, Integer> items;

    private String discountCode;
}
