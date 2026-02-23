package com.supermarket.billing.client;

import com.supermarket.billing.dto.DiscountApplicationRequest;
import com.supermarket.billing.dto.DiscountApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class DiscountsClient {

    private static final String APPLY_PATH = "/api/v1/discounts/apply";

    @Qualifier("discountsRestClient")
    private final RestClient restClient;

    public DiscountApplicationResponse applyDiscount(DiscountApplicationRequest request) {
        return restClient.post()
                .uri(APPLY_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(DiscountApplicationResponse.class);
    }
}

