package com.supermarket.discounts.controller;

import com.supermarket.discounts.dto.DiscountApplicationRequest;
import com.supermarket.discounts.dto.DiscountApplicationResponse;
import com.supermarket.discounts.dto.DiscountRequest;
import com.supermarket.discounts.dto.DiscountResponse;
import com.supermarket.discounts.service.DiscountService;
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
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountResponse> createDiscount(@Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.createDiscount(request));
    }

    @GetMapping("/{code}")
    public ResponseEntity<DiscountResponse> getDiscount(@PathVariable String code) {
        return ResponseEntity.ok(discountService.getDiscount(code));
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponse>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponse>> getActiveDiscounts() {
        return ResponseEntity.ok(discountService.getActiveDiscounts());
    }

    @PutMapping("/{code}")
    public ResponseEntity<DiscountResponse> updateDiscount(
            @PathVariable String code,
            @Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(discountService.updateDiscount(code, request));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable String code) {
        discountService.getDiscount(code);
        discountService.deleteDiscount(code);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<DiscountApplicationResponse> applyDiscount(
            @Valid @RequestBody DiscountApplicationRequest request) {
        return ResponseEntity.ok(discountService.applyDiscount(request));
    }
}
