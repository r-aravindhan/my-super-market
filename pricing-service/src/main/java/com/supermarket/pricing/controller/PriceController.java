package com.supermarket.pricing.controller;

import com.supermarket.pricing.dto.PriceCalculationRequest;
import com.supermarket.pricing.dto.PriceCalculationResponse;
import com.supermarket.pricing.dto.PriceRequest;
import com.supermarket.pricing.dto.PriceResponse;
import com.supermarket.pricing.service.PriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @PostMapping
    public ResponseEntity<PriceResponse> createPrice(@Valid @RequestBody PriceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(priceService.createPrice(request));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<PriceResponse> getPrice(@PathVariable String sku) {
        return ResponseEntity.ok(priceService.getPrice(sku));
    }

    @GetMapping
    public ResponseEntity<List<PriceResponse>> getAllPrices() {
        return ResponseEntity.ok(priceService.getAllPrices());
    }

    @PutMapping("/{sku}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable String sku,
            @Valid @RequestBody PriceRequest request) {
        return ResponseEntity.ok(priceService.updatePrice(sku, request));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deletePrice(@PathVariable String sku) {
        priceService.getPrice(sku);
        priceService.deletePrice(sku);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrices(
            @Valid @RequestBody PriceCalculationRequest request) {
        return ResponseEntity.ok(priceService.calculatePrices(request));
    }
}
