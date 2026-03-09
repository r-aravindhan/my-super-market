package com.supermarket.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckResponse {

    private boolean available;
    private Map<String, String> insufficientItems;
}
