package com.supermarket.billing.controller;

import com.supermarket.billing.dto.BillRequest;
import com.supermarket.billing.dto.BillResponse;
import com.supermarket.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(@Valid @RequestBody BillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createBill(request));
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse> getBill(@PathVariable String billId) {
        return ResponseEntity.ok(billingService.getBill(billId));
    }
}
