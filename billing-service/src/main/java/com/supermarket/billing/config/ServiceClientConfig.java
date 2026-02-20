package com.supermarket.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ServiceClientConfig {

    @Value("${services.inventory.url:http://localhost:8081}")
    private String inventoryServiceUrl;

    @Value("${services.pricing.url:http://localhost:8082}")
    private String pricingServiceUrl;

    @Value("${services.discounts.url:http://localhost:8083}")
    private String discountsServiceUrl;

    @Bean("inventoryRestClient")
    public RestClient inventoryRestClient(RestClient.Builder builder) {
        return builder.baseUrl(inventoryServiceUrl).build();
    }

    @Bean("pricingRestClient")
    public RestClient pricingRestClient(RestClient.Builder builder) {
        return builder.baseUrl(pricingServiceUrl).build();
    }

    @Bean("discountsRestClient")
    public RestClient discountsRestClient(RestClient.Builder builder) {
        return builder.baseUrl(discountsServiceUrl).build();
    }
}
