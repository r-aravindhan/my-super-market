package com.supermarket.billing.client;

import com.supermarket.billing.dto.PriceCalculationRequest;
import com.supermarket.billing.dto.PriceCalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PricingClient {

    private static final String CALCULATE_PATH = "/api/v1/prices/calculate";

    @Qualifier("pricingRestClient")
    private final RestClient restClient;

    public PriceCalculationResponse calculatePrices(PriceCalculationRequest request) {
        return restClient.post()
                .uri(CALCULATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PriceCalculationResponse.class);
    }
}
