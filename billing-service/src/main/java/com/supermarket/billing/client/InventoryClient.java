package com.supermarket.billing.client;

import com.supermarket.billing.dto.StockCheckRequest;
import com.supermarket.billing.dto.StockCheckResponse;
import com.supermarket.billing.dto.StockReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private static final String STOCK_CHECK_PATH = "/api/v1/products/stock/check";
    private static final String STOCK_RESERVE_PATH = "/api/v1/products/stock/reserve";
    private static final String STOCK_RELEASE_PATH = "/api/v1/products/stock/release";

    @Qualifier("inventoryRestClient")
    private final RestClient restClient;

    public StockCheckResponse checkStock(StockCheckRequest request) {
        return restClient.post()
                .uri(STOCK_CHECK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(StockCheckResponse.class);
    }

    public void reserveStock(StockReservationRequest request) {
        restClient.post()
                .uri(STOCK_RESERVE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseStock(StockReservationRequest request) {
        restClient.post()
                .uri(STOCK_RELEASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
