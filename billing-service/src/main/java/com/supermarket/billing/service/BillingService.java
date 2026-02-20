package com.supermarket.billing.service;

import com.supermarket.billing.client.DiscountsClient;
import com.supermarket.billing.client.InventoryClient;
import com.supermarket.billing.client.PricingClient;
import com.supermarket.billing.dto.BillRequest;
import com.supermarket.billing.dto.BillResponse;
import com.supermarket.billing.dto.DiscountApplicationRequest;
import com.supermarket.billing.dto.DiscountApplicationResponse;
import com.supermarket.billing.dto.PriceCalculationRequest;
import com.supermarket.billing.dto.PriceCalculationResponse;
import com.supermarket.billing.dto.StockCheckRequest;
import com.supermarket.billing.dto.StockCheckResponse;
import com.supermarket.billing.dto.StockReservationRequest;
import com.supermarket.billing.entity.Bill;
import com.supermarket.billing.exception.BillingException;
import com.supermarket.billing.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final InventoryClient inventoryClient;
    private final PricingClient pricingClient;
    private final DiscountsClient discountsClient;
    private final BillRepository billRepository;

    @Transactional
    public BillResponse createBill(BillRequest request) {
        String billId = UUID.randomUUID().toString();
        String reservationId = "RES-" + billId;
        boolean stockReserved = false;

        try {
            StockCheckRequest stockCheckRequest = StockCheckRequest.builder()
                    .items(request.getItems())
                    .build();
            StockCheckResponse stockCheck = inventoryClient.checkStock(stockCheckRequest);

            if (!stockCheck.isAvailable()) {
                throw new BillingException("Insufficient stock: " + stockCheck.getInsufficientItems());
            }

            PriceCalculationRequest priceRequest = PriceCalculationRequest.builder()
                    .items(request.getItems())
                    .build();
            PriceCalculationResponse priceResponse = pricingClient.calculatePrices(priceRequest);

            DiscountApplicationResponse discountResponse = null;
            if (request.getDiscountCode() != null && !request.getDiscountCode().isBlank()) {
                try {
                    DiscountApplicationRequest discountRequest = DiscountApplicationRequest.builder()
                            .items(request.getItems())
                            .itemPrices(priceResponse.getItemPrices())
                            .subtotal(priceResponse.getSubtotal())
                            .discountCode(request.getDiscountCode())
                            .build();
                    discountResponse = discountsClient.applyDiscount(discountRequest);
                } catch (Exception e) {
                    log.warn("Failed to apply discount: {}", e.getMessage());
                    throw new BillingException("Invalid or expired discount code: " + request.getDiscountCode());
                }
            }

            StockReservationRequest reserveRequest = StockReservationRequest.builder()
                    .reservationId(reservationId)
                    .items(request.getItems())
                    .build();
            inventoryClient.reserveStock(reserveRequest);
            stockReserved = true;

            BigDecimal total = priceResponse.getSubtotal();
            BigDecimal discountAmount = BigDecimal.ZERO;
            String appliedDiscountCode = null;

            if (discountResponse != null) {
                total = discountResponse.getTotalAfterDiscount();
                discountAmount = discountResponse.getDiscountAmount();
                appliedDiscountCode = discountResponse.getAppliedDiscountCode();
            }

            Bill bill = Bill.builder()
                    .id(billId)
                    .items(request.getItems())
                    .itemPrices(priceResponse.getItemPrices())
                    .subtotal(priceResponse.getSubtotal())
                    .discountAmount(discountAmount)
                    .total(total)
                    .currency(priceResponse.getCurrency())
                    .appliedDiscountCode(appliedDiscountCode)
                    .createdAt(Instant.now())
                    .build();

            billRepository.save(bill);

            return BillResponse.builder()
                    .billId(billId)
                    .items(bill.getItems())
                    .itemPrices(bill.getItemPrices())
                    .subtotal(bill.getSubtotal())
                    .discountAmount(bill.getDiscountAmount())
                    .total(bill.getTotal())
                    .currency(bill.getCurrency())
                    .appliedDiscountCode(bill.getAppliedDiscountCode())
                    .build();
        } catch (BillingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create bill", e);
            if (stockReserved) {
                try {
                    inventoryClient.releaseStock(StockReservationRequest.builder()
                            .reservationId(reservationId)
                            .items(request.getItems())
                            .build());
                } catch (Exception releaseEx) {
                    log.error("Failed to release reservation after billing failure", releaseEx);
                }
            }
            throw new BillingException("Failed to process bill: " + e.getMessage());
        }
    }

    public BillResponse getBill(String billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillingException("Bill not found: " + billId));

        return BillResponse.builder()
                .billId(bill.getId())
                .items(bill.getItems())
                .itemPrices(bill.getItemPrices())
                .subtotal(bill.getSubtotal())
                .discountAmount(bill.getDiscountAmount())
                .total(bill.getTotal())
                .currency(bill.getCurrency())
                .appliedDiscountCode(bill.getAppliedDiscountCode())
                .build();
    }
}
