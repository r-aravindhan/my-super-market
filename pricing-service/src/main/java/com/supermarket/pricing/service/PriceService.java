package com.supermarket.pricing.service;

import com.supermarket.pricing.dto.PriceCalculationRequest;
import com.supermarket.pricing.dto.PriceCalculationResponse;
import com.supermarket.pricing.dto.PriceRequest;
import com.supermarket.pricing.dto.PriceResponse;
import com.supermarket.pricing.entity.Price;
import com.supermarket.pricing.exception.ResourceNotFoundException;
import com.supermarket.pricing.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final PriceRepository priceRepository;

    @Transactional
    public PriceResponse createPrice(PriceRequest request) {
        Price price = Price.builder()
                .sku(request.getSku())
                .unitPrice(request.getUnitPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .build();
        return toResponse(priceRepository.save(price));
    }

    public PriceResponse getPrice(String sku) {
        return toResponse(findBySku(sku));
    }

    public List<PriceResponse> getAllPrices() {
        return priceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PriceResponse updatePrice(String sku, PriceRequest request) {
        Price price = findBySku(sku);
        price.setUnitPrice(request.getUnitPrice());
        price.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        return toResponse(priceRepository.save(price));
    }

    @Transactional
    public void deletePrice(String sku) {
        Price price = findBySku(sku);
        priceRepository.delete(price);
    }

    public PriceCalculationResponse calculatePrices(PriceCalculationRequest request) {
        Map<String, BigDecimal> itemPrices = new HashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        String currency = "USD";

        for (Map.Entry<String, Integer> entry : request.getItems().entrySet()) {
            Price price = findBySku(entry.getKey());
            BigDecimal lineTotal = price.getUnitPrice()
                    .multiply(BigDecimal.valueOf(entry.getValue()))
                    .setScale(SCALE, ROUNDING_MODE);
            itemPrices.put(entry.getKey(), lineTotal);
            subtotal = subtotal.add(lineTotal);
            currency = price.getCurrency();
        }

        return PriceCalculationResponse.builder()
                .itemPrices(itemPrices)
                .subtotal(subtotal)
                .currency(currency)
                .build();
    }

    private Price findBySku(String sku) {
        return priceRepository.findById(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Price", sku));
    }

    private PriceResponse toResponse(Price price) {
        return PriceResponse.builder()
                .sku(price.getSku())
                .unitPrice(price.getUnitPrice())
                .currency(price.getCurrency())
                .build();
    }
}
